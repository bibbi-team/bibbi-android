package com.no5ing.bbibbi.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.APIResponse.Companion.onFailed
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.Resource
import com.no5ing.bbibbi.data.model.ResourceError
import com.no5ing.bbibbi.data.model.Status
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import timber.log.Timber

abstract class BasePageSource<T : BaseModel>(
    private val arguments: Arguments,
) : PagingSource<Int, T>() {
    private val resource by lazy {
        Resource<Pagination<T>>()
    }

    companion object {
        const val PAGE_START_INDEX = 1
    }

    private fun handleResponse(result: ApiResponse<Pagination<T>>) {
        result.onSuccess {
            resource.success(this.data)
        }.onFailed {
            resource.error(it)
        }.onException {
            throwable.printStackTrace()
            resource.error(
                ResourceError(
                    errorCode = "UNKNOWN",
                    message = throwable.message ?: "Unknown error"
                )
            )
        }
    }

    abstract suspend fun requestAPI(
        arguments: Arguments,
        loadParams: LoadParams<Int>
    ): ApiResponse<Pagination<T>>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: PAGE_START_INDEX
        val apiResult = requestAPI(
            arguments = arguments,
            loadParams = params,
        )
        handleResponse(apiResult)
        return when (resource.status) {
            Status.SUCCESS -> {
                val result = resource.data ?: throw RuntimeException()
                LoadResult.Page(
                    data = result.results,
                    prevKey = if (page == PAGE_START_INDEX) null else page - 1,
                    nextKey = if (result.hasNext) result.currentPage + 1 else null
                )
            }

            Status.ERROR, Status.LOADING -> {
                Timber.e("[BasePageSource] ${resource.error}")
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (page == PAGE_START_INDEX) null else page - 1,
                    nextKey = page + 1
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}