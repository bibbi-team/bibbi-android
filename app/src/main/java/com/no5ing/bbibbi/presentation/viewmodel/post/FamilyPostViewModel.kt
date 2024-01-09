package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyPostViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<MainFeedUiState>>() {
    override fun initState(): APIResponse<MainFeedUiState> {
        return APIResponse.loading()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val resId = arguments.resourceId ?: throw RuntimeException()
            val post = restAPI.getPostApi().getPost(resId)
            val viewState = post.suspendMapSuccess {
                val member = kotlin.runCatching {
                    memberCacheProvider.getMember(this.authorId)
                }
                MainFeedUiState(
                    writer = member.getOrElse { com.no5ing.bbibbi.data.model.member.Member.unknown() },
                    post = this
                )
            }
            setState(viewState.wrapToAPIResponse())
        }
    }

}