package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FamilySwipePostsViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<List<MainFeedUiState>>>() {
    override fun initState(): APIResponse<List<MainFeedUiState>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val date = arguments.get("date") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            restAPI.getPostApi().getPosts(
                page = 1,
                size = 50,
                date = date,
                memberId = null,
            ).suspendMapSuccess {
                val posts = this.results.map {
                    val member = kotlin.runCatching {
                        memberCacheProvider.getMember(it.authorId)
                    }
                    MainFeedUiState(
                        writer = member.getOrElse { com.no5ing.bbibbi.data.model.member.Member.unknown() },
                        post = it
                    )
                }
                setState(APIResponse.success(posts))
            }
        }
    }

    override fun release() {
        super.release()
    }
}