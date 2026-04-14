package com.no5ing.bbibbi.data.datasource.network

import com.no5ing.bbibbi.data.datasource.network.response.KakaoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApi {

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("x") x: String? = null,
        @Query("y") y: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): KakaoSearchResponse
}
