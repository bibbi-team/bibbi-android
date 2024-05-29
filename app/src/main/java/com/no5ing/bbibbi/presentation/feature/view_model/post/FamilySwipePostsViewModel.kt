package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.uistate.post.CalendarFeedUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.skydoves.sandwich.suspendMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FamilySwipePostsViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<List<CalendarFeedUiState>>>() {
    override fun initState(): APIResponse<List<CalendarFeedUiState>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val date = arguments.get("date")?.let(LocalDate::parse) ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            restAPI.getPostApi().getDailyCalendar(
                date = date,
            ).suspendMapSuccess {
                val posts = this.results.map {
                    val member = kotlin.runCatching {
                        memberCacheProvider.getMember(it.authorId)
                    }
                    CalendarFeedUiState(
                        writer = member.getOrElse { Member.unknown() },
                        post = it
                    )
                }
                setState(APIResponse.success(posts))
            }
        }
    }

}