package com.no5ing.bbibbi.data.datasource.local

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.mission.Mission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionCacheProvider @Inject constructor(
    private val restApi: RestAPI,
) {
    private val cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(object : CacheLoader<String, Mission>() {
            override fun load(key: String): Mission {
                return runBlocking {
                    restApi.getPostApi().getMissionById(key).wrapToAPIResponse().data
                }
            }
        })

    suspend fun getMission(missionId: String): Mission? {
        return coroutineScope {
            withContext(Dispatchers.IO) {
                runCatching {
                    return@runCatching cache.get(missionId)
                }.getOrNull()
            }
        }
    }
}