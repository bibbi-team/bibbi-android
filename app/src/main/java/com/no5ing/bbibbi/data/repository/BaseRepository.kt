package com.no5ing.bbibbi.data.repository

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
abstract class BaseRepository<T> {
    @Inject
    lateinit var restAPI: RestAPI

    abstract fun fetch(arguments: Arguments): Flow<T>

    open fun closeResources() {
        //Timber.d("[BaseRepository] closeResources")
    }
}