package com.no5ing.bbibbi.presentation.feature.view_model.post

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.post.GetFeedsRepository
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainPostFeedViewModel @Inject constructor(
    private val getPostsRepository: GetFeedsRepository,
) : BaseViewModel<PagingData<MainFeedUiState>>() {
    override fun initState(): PagingData<MainFeedUiState> {
        return PagingData.empty()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            getPostsRepository
                .fetch(arguments)
                .cachedIn(viewModelScope)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PagingData.empty()
                ).collectLatest {
                    setState(it)
                }
        }
    }

    fun refresh() {
        getPostsRepository.pagingSource.invalidate()
    }

    override fun release() {
        super.release()
        getPostsRepository.closeResources()
    }
}