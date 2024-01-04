package com.no5ing.bbibbi.data.model

import com.google.gson.Gson
import com.no5ing.bbibbi.data.datasource.network.response.ErrorResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.retrofit.errorBody

class APIResponse<T : Any>(
    val status: Status,
    data: T?,
    val errorCode: String? = null,
) {
    init {
        data?.let {
            this.data = it
        }
    }

    lateinit var data: T
        private set

    fun isIdle() = status == Status.IDLE

    fun isLoading() = status == Status.LOADING

    fun isReady() = status == Status.SUCCESS || status == Status.SUCCESS_EMPTY

    fun isFailed() = status == Status.ERROR

    companion object {
        fun <T> ApiResponse<T>.onFailed(onError: (ResourceError) -> Unit): ApiResponse<T> {
            this.onError {
                val errResponse = try {
                    Gson().fromJson(errorBody?.charStream(), ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                onError(
                    ResourceError(
                        errorCode = errResponse?.code ?: "",
                        message = errResponse?.message ?: "Unknown Error"
                    )
                )
            }
            return this
        }

        inline fun <reified T : Any> loading() = APIResponse<T>(
            status = Status.LOADING,
            data = null,
        )

        inline fun <reified T : Any> idle() = APIResponse<T>(
            status = Status.IDLE,
            data = null,
        )

        inline fun <reified T : Any> unknownError() = APIResponse<T>(
            status = Status.ERROR,
            data = null,
        )

        inline fun <reified T : Any> errorOfOther(
            otherResponse: ApiResponse.Failure.Error
        ): APIResponse<T> {
            val errorCode = try {
                Gson().fromJson(otherResponse.errorBody?.charStream(), ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            return APIResponse<T>(
                status = Status.ERROR,
                data = null,
                errorCode = errorCode?.code,
            )
        }

        fun <T : Any> ApiResponse<T>.wrapToAPIResponse(): APIResponse<T> {
            return when (this) {
                is ApiResponse.Success -> {
                    APIResponse(
                        status = Status.SUCCESS,
                        data = this.data,
                    )
                }

                is ApiResponse.Failure.Error -> {
                    val error: ErrorResponse? =
                        try {
                            Gson().fromJson(errorBody?.charStream(), ErrorResponse::class.java)
                        } catch (e: Exception) {
                            null
                        }
                    APIResponse(
                        status = Status.ERROR,
                        data = null,
                        errorCode = error?.code
                    )
                }

                is ApiResponse.Failure.Exception -> {
                    throwable.printStackTrace()
                    APIResponse(
                        status = Status.ERROR,
                        data = null,
                    )
                }
            }
        }
    }

    sealed class Status {
        object SUCCESS : Status()
        object SUCCESS_EMPTY : Status()
        object ERROR : Status()
        object LOADING : Status()
        object IDLE : Status()
    }
}