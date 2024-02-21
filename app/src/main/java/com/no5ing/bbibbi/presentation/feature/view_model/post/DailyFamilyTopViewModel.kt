package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedStoryElementUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.no5ing.bbibbi.util.todayAsString
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class DailyFamilyTopViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<List<MainFeedStoryElementUiState>>>() {
    override fun initState(): APIResponse<List<MainFeedStoryElementUiState>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val newList = ArrayList<MainFeedStoryElementUiState>()
            val members = async {
                restAPI.getMemberApi()
                    .getMembers(
                        page = 1,
                        size = 100
                    )
            }
            restAPI
                .getPostApi()
                .getPosts(
                    page = 1,
                    size = 100,
                    memberId = null,
                    date = todayAsString(),
                    sort = "ASC"
                ).suspendOnSuccess {
                    val response = members.await()
                    if (response.isSuccess) {
                        val memberMap = (response.getOrNull()?.results?.associateBy { it.memberId } ?: emptyMap())
                            .toMutableMap()
                        data.results.forEachIndexed { index, post ->
                            val currentMember = memberMap.remove(post.authorId) ?: return@forEachIndexed
                            newList.add(MainFeedStoryElementUiState(currentMember, true))
                        }
                        memberMap.forEach {
                            newList.add(MainFeedStoryElementUiState(it.value, false))
                        }
                        setState(APIResponse.success(newList))
                    } else {
                        setState(APIResponse.unknownError())
                    }
                }.suspendOnFailure {
                    setState(APIResponse.unknownError())
                }
        }
    }

}