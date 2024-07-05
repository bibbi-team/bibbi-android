package com.no5ing.bbibbi.presentation.feature.view_model

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.view.FamilyInviteModel
import com.no5ing.bbibbi.data.repository.Arguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AlreadyFamilyExistsViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<FamilyInviteModel>>() {

    override fun initState(): APIResponse<FamilyInviteModel> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val linkId = arguments.get("linkId") ?: throw RuntimeException()
            val mainResult = restAPI.getViewApi().getFamilyInviteView(linkId)
            val apiResult = mainResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}