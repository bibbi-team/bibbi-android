package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.skydoves.sandwich.suspendMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

typealias CalenderDetailContentUiState = Triple<MainFeedUiState?, MainFeedUiState?, MainFeedUiState?>

@HiltViewModel
class CalendarDetailContentViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<CalenderDetailContentUiState>>() {
    private val detailCache = mutableMapOf<String, MainFeedUiState>()
    override fun initState(): APIResponse<CalenderDetailContentUiState> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val left = arguments.get("left")
        val right = arguments.get("right")
        withMutexScope(Dispatchers.IO) {
            val resId = arguments.resourceId ?: throw RuntimeException()
            val mainPost = getOrFetch(resId)
            val leftPost = getOrFetch(left)
            val rightPost = getOrFetch(right)
            val mainPostResult = CalenderDetailContentUiState(
                first = leftPost.await(),
                second = mainPost.await(),
                third = rightPost.await()
            )
            setState(APIResponse.success(mainPostResult))
        }
    }

    private fun CoroutineScope.getOrFetch(postId: String?): Deferred<MainFeedUiState?> = async {
        postId?.let {
            val previous = detailCache[it]
            if (previous != null) return@let previous
            val currentPost = restAPI.getPostApi().getPost(it)
            val results = currentPost.suspendMapSuccess {
                val member = kotlin.runCatching {
                    memberCacheProvider.getMember(this.authorId)
                }
                val uiState = MainFeedUiState(
                    writer = member.getOrElse { Member.unknown() },
                    post = this
                )
                detailCache[it] = uiState
                uiState
            }.wrapToAPIResponse()
            if (results.isReady()) results.data else null
        }
    }

}