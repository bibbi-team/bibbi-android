package com.no5ing.bbibbi.data.repository.post

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.BasePageSource
import com.no5ing.bbibbi.data.repository.BaseRepository
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPostsRepository @Inject constructor() : BaseRepository<PagingData<Post>>() {
    private lateinit var pagingSource: GetPostPagingSource
    override fun fetch(arguments: Arguments): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 5
            )
        ) {
            pagingSource = GetPostPagingSource(restAPI, arguments)
            pagingSource
        }.flow
    }

    override fun closeResources() {
        super.closeResources()
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }
}

class GetPostPagingSource @Inject constructor(
    private val restAPI: RestAPI,
    arguments: Arguments
) : BasePageSource<Post>(arguments) {
    override suspend fun requestAPI(
        arguments: Arguments,
        loadParams: LoadParams<Int>
    ): ApiResponse<Pagination<Post>> {
        return restAPI.getPostApi().getPosts(
            date = null,
            memberId = arguments.get("memberId"),
            page = loadParams.key ?: 1,
            size = loadParams.loadSize
        )
    }

}