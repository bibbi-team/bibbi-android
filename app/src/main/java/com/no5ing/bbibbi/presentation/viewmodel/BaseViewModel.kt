package com.no5ing.bbibbi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.model.Resource
import com.no5ing.bbibbi.data.repository.Arguments
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Singleton

@Singleton
abstract class BaseViewModel<T> : ViewModel() {
    init {
        super.addCloseable(::releaseInternal)
        Timber.d("[InitViewModel] $this")
    }

    private val _uiState = MutableStateFlow<T>(initState())
    val uiState: StateFlow<T> = _uiState

    private val isInitialized = MutableStateFlow(false)

    fun isInitialize(): Boolean {
        if (!isInitialized.value) {
            isInitialized.value = true
            return true
        }
        return false
    }

    val resource by lazy {
        Resource<T>()
    }

    private val mutex by lazy {
        Mutex()
    }

    abstract fun initState(): T

    fun setState(newState: T) {
        _uiState.value = newState
    }

    fun resetState() {
        _uiState.value = initState()
    }

    fun withMutexScope(
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        condition: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (!condition) {
            Timber.e("[BaseViewModel] invoke condition failed : ${this}")
            return
        }
        viewModelScope.launch(dispatcher) {
            mutex.withLock {
                block()
            }
        }
    }

    abstract fun invoke(arguments: Arguments)

    private fun releaseInternal() {
        Timber.d("[ReleaseViewModel] $this")
        this.release()
    }

    open fun release() {
    }
}