package com.no5ing.bbibbi.data.repository.post

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.BasePageSource
import com.no5ing.bbibbi.data.repository.BaseRepository
import com.no5ing.bbibbi.presentation.feature.uistate.post.PostCommentUiState
import com.no5ing.bbibbi.util.parallelMap
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.mapSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCommentsRepository @Inject constructor(
    private val memberCacheProvider: MemberCacheProvider,
) : BaseRepository<PagingData<PostCommentUiState>>() {
    private lateinit var pagingSource: GetCommentsPageSource
    override fun fetch(arguments: Arguments): Flow<PagingData<PostCommentUiState>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 5
            )
        ) {
            pagingSource = GetCommentsPageSource(restAPI, memberCacheProvider, arguments)
            pagingSource
        }.flow
    }

    override fun closeResources() {
        super.closeResources()
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }
}

class GetCommentsPageSource @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
    arguments: Arguments
) : BasePageSource<PostCommentUiState>(arguments) {
    override suspend fun requestAPI(
        arguments: Arguments,
        loadParams: LoadParams<Int>
    ): ApiResponse<Pagination<PostCommentUiState>> {
        val postComments = restAPI.getPostApi().getPostComments(
            postId = arguments.get("postId") ?: throw RuntimeException(),
            page = loadParams.key ?: 1,
            size = loadParams.loadSize,
            sort = "ASC"
        )
        return postComments.mapSuccess {
            Pagination<PostCommentUiState>(
                currentPage = currentPage,
                totalPage = totalPage,
                itemPerPage = itemPerPage,
                hasNext = hasNext,
                results = results.parallelMap(Dispatchers.IO) {
                    val member = kotlin.runCatching {
                        memberCacheProvider.getMember(it.memberId)
                    }
                    PostCommentUiState(
                        commentId = it.commentId,
                        postId = it.postId,
                        memberId = it.memberId,
                        content = it.comment,
                        createdAt = it.createdAt,
                        member = member.getOrNull()
                    )
                }
            )
        }
    }

}