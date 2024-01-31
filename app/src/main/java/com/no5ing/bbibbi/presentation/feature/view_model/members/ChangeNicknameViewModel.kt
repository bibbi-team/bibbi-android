package com.no5ing.bbibbi.presentation.feature.view_model.members

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeNameRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ChangeNicknameViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Member>>() {
    override fun initState(): APIResponse<Member> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        val nickName = arguments.get("nickName") ?: throw RuntimeException()
        setState(loading())
        withMutexScope(Dispatchers.IO) {
            val result = restAPI
                .getMemberApi()
                .changeMemberName(
                    memberId = memberId,
                    body = ChangeNameRequest(
                        name = nickName,
                    )
                ).wrapToAPIResponse()
            setState(result)
        }
    }

}