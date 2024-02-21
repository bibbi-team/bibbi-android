package com.no5ing.bbibbi.data.repository.post

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.BasePageSource
import com.no5ing.bbibbi.data.repository.BaseRepository
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.util.parallelMap
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.mapSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFeedsRepository @Inject constructor(
    private val memberCacheProvider: MemberCacheProvider,
) : BaseRepository<PagingData<MainFeedUiState>>() {
    private lateinit var pagingSource: GetFeedPageSource
    override fun fetch(arguments: Arguments): Flow<PagingData<MainFeedUiState>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 5
            )
        ) {
            pagingSource = GetFeedPageSource(restAPI, memberCacheProvider, arguments)
            pagingSource
        }.flow
    }

    fun invalidateSource() {
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }

    override fun closeResources() {
        super.closeResources()
        this.invalidateSource()
    }
}

class GetFeedPageSource @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
    arguments: Arguments
) : BasePageSource<MainFeedUiState>(arguments) {
    override suspend fun requestAPI(
        arguments: Arguments,
        loadParams: LoadParams<Int>
    ): ApiResponse<Pagination<MainFeedUiState>> {
        val posts = restAPI.getPostApi().getPosts(
            date = arguments.get("date"),
            memberId = arguments.get("memberId"),
            page = loadParams.key ?: 1,
            size = loadParams.loadSize
        )
        return posts.mapSuccess {
            Pagination<MainFeedUiState>(
                currentPage = currentPage,
                totalPage = totalPage,
                itemPerPage = itemPerPage,
                hasNext = hasNext,
                results = results.parallelMap(Dispatchers.IO) {
                    val member = kotlin.runCatching {
                        memberCacheProvider.getMember(it.authorId)
                    }
                    MainFeedUiState(
                        writer = member.getOrElse { Member.unknown() },
                        post = it
                    )
                }
            )
        }
    }

}