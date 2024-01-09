package com.no5ing.bbibbi.data.repository.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.BasePageSource
import com.no5ing.bbibbi.data.repository.BaseRepository
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMembersRepository @Inject constructor() : BaseRepository<PagingData<Member>>() {
    private lateinit var pagingSource: GetMembersPageSource
    override fun fetch(arguments: Arguments): Flow<PagingData<Member>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5
            )
        ) {
            pagingSource = GetMembersPageSource(restAPI, arguments)
            pagingSource
        }.flow
    }

    override fun closeResources() {
        super.closeResources()
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }
}

class GetMembersPageSource @Inject constructor(
    private val restAPI: RestAPI,
    arguments: Arguments
) : BasePageSource<Member>(arguments) {
    override suspend fun requestAPI(
        arguments: Arguments,
        loadParams: LoadParams<Int>
    ): ApiResponse<Pagination<Member>> {
        return restAPI.getMemberApi().getMembers(
            page = loadParams.key ?: 1,
            size = loadParams.loadSize
        )
    }

}