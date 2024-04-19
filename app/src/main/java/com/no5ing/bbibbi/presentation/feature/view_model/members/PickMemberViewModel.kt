package com.no5ing.bbibbi.presentation.feature.view_model.members

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class PickMemberViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<DefaultResponse>>() {
    override fun initState(): APIResponse<DefaultResponse> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        setState(loading())
        withMutexScope(Dispatchers.IO) {
            val result = restAPI
                .getMemberApi()
                .pickMember(
                    memberId = memberId,
                ).wrapToAPIResponse()
            setState(result)
        }
    }

}