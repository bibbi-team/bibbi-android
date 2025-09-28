package com.no5ing.bbibbi.presentation.feature.view_model.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.FamilySummary
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.post.GetAIPostsRepository
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GetAIPostsViewModel @Inject constructor(
    private val getPostsRepository: GetAIPostsRepository,
) : BaseViewModel<PagingData<AIPost>>() {
    override fun initState(): PagingData<AIPost> {
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
        getPostsRepository.invalidateSource()
    }

    override fun release() {
        super.release()
        getPostsRepository.closeResources()
    }
}