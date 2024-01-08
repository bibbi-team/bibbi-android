package com.no5ing.bbibbi.data.datasource.local

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberCacheProvider @Inject constructor(
    private val restApi: RestAPI,
) {
    private val cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(3, TimeUnit.MINUTES)
        .build(object : CacheLoader<String, Member>() {
            override fun load(key: String): Member {
                return runBlocking {
                    restApi.getMemberApi().getMember(key).wrapToAPIResponse().data
                }
            }
        })

    suspend fun getMember(memberId: String): Member {
        return cache.get(memberId)
    }
}