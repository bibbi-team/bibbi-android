package com.no5ing.bbibbi.presentation.feature.view_model.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.post.GetCommentsRepository
import com.no5ing.bbibbi.presentation.feature.uistate.post.PostCommentUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PostCommentViewModel @Inject constructor(
    private val getCommentsRepository: GetCommentsRepository,
) : BaseViewModel<PagingData<PostCommentUiState>>() {
    private val _currentQuery = MutableLiveData<Arguments>()
    val currentQuery: LiveData<Arguments> = _currentQuery

    val commentLiveData: Flow<PagingData<PostCommentUiState>> = currentQuery.switchMap { arguments ->
        getCommentsRepository
            .fetch(arguments)
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PagingData.empty()
            ).asLiveData(Dispatchers.IO)
    }.asFlow()

    override fun initState(): PagingData<PostCommentUiState> {
        return PagingData.empty()
    }

    fun refresh() = getCommentsRepository.invalidateSource()

    override fun invoke(arguments: Arguments) {
        _currentQuery.postValue(arguments)
    }

    override fun release() {
        super.release()
        getCommentsRepository.closeResources()
    }

}