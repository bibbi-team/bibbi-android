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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias CalenderDetailContentUiState = Triple<MainFeedUiState?, MainFeedUiState?, MainFeedUiState?>
@HiltViewModel
class CalendarDetailContentViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<CalenderDetailContentUiState>>() {
    override fun initState(): APIResponse<CalenderDetailContentUiState> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val left = arguments.get("left")
        val right = arguments.get("right")
        Timber.d("left: $left, right: $right")
        viewModelScope.launch(Dispatchers.IO) {
            val resId = arguments.resourceId ?: throw RuntimeException()
            val mainPost = async {
                val post = restAPI.getPostApi().getPost(resId)
                post.suspendMapSuccess {
                    val member = kotlin.runCatching {
                        memberCacheProvider.getMember(this.authorId)
                    }
                    MainFeedUiState(
                        writer = member.getOrElse { com.no5ing.bbibbi.data.model.member.Member.unknown() },
                        post = this
                    )
                }
            }

            val leftPost = async {
                left?.let {
                    val currentPost = restAPI.getPostApi().getPost(it)
                    val results = currentPost.suspendMapSuccess {
                        val member = kotlin.runCatching {
                            memberCacheProvider.getMember(this.authorId)
                        }
                        MainFeedUiState(
                            writer = member.getOrElse { com.no5ing.bbibbi.data.model.member.Member.unknown() },
                            post = this
                        )
                    }.wrapToAPIResponse()
                    if (results.isReady()) results.data else null
                }
            }
            val rightPost = async {
                right?.let {
                    val currentPost = restAPI.getPostApi().getPost(it)
                    val results = currentPost.suspendMapSuccess {
                        val member = kotlin.runCatching {
                            memberCacheProvider.getMember(this.authorId)
                        }
                        MainFeedUiState(
                            writer = member.getOrElse { com.no5ing.bbibbi.data.model.member.Member.unknown() },
                            post = this
                        )
                    }.wrapToAPIResponse()
                    if (results.isReady()) results.data else null
                }
            }
            val mainPostResult = mainPost.await().suspendMapSuccess {
                CalenderDetailContentUiState(
                    first = leftPost.await(),
                    second = this,
                    third = rightPost.await()
                )
            }
            setState(mainPostResult.wrapToAPIResponse())
        }
    }

}