package com.no5ing.bbibbi.presentation.viewmodel.members

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeNameRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNicknameViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Member>>() {
    val me = localDataStorage.getMe()
    override fun initState(): APIResponse<Member> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val nickName = arguments.get("nickName") ?: throw RuntimeException()
        setState(loading())
        viewModelScope.launch(Dispatchers.IO) {
            val result = restAPI
                .getMemberApi()
                .changeMemberName(
                    memberId = me?.memberId ?: throw RuntimeException(),
                    body = ChangeNameRequest(
                        name = nickName,
                    )
                ).suspendOnSuccess {
                    localDataStorage.setMe(data)
                }.wrapToAPIResponse()
            setState(result)
        }
    }

}