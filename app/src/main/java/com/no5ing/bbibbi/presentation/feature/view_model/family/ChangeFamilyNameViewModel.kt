package com.no5ing.bbibbi.presentation.feature.view_model.family

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeFamilyNameRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ChangeFamilyNameViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Family>>() {
    override fun initState(): APIResponse<Family> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val familyId = arguments.get("familyId") ?: throw RuntimeException()
        val familyName = arguments.get("familyName")
        setState(loading())
        withMutexScope(Dispatchers.IO) {
            val result = restAPI
                .getFamilyApi()
                .updateFamilyName(
                    familyId = familyId,
                    body = ChangeFamilyNameRequest(
                        familyName = familyName,
                    )
                ).wrapToAPIResponse()
            setState(result)
        }
    }

}