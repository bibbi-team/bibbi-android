package com.no5ing.bbibbi.presentation.feature.viewmodel.post

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.post.GetPostsRepository
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FamilyPostsViewModel @Inject constructor(
    private val getPostsRepository: GetPostsRepository,
) : BaseViewModel<PagingData<Post>>() {
    override fun initState(): PagingData<Post> {
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

    override fun release() {
        super.release()
        getPostsRepository.closeResources()
    }
}