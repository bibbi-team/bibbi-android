package com.no5ing.bbibbi.presentation.viewmodel.members

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.mapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewReactionMemberViewModel @Inject constructor(
    private val localDataStorage: LocalDataStorage,
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Map<String, Member>>>() {
    val me = localDataStorage.getMe()

    override fun initState(): APIResponse<Map<String, Member>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        viewModelScope.launch(Dispatchers.IO) {
            setState(restAPI.getMemberApi().getMembers(
                page = 1,
                size = 100
            ).mapSuccess {
                results.associateBy { it.memberId }
            }.wrapToAPIResponse())
        }
    }

}