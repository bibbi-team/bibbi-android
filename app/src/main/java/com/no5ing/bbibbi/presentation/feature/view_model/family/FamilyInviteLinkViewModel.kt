package com.no5ing.bbibbi.presentation.feature.view_model.family

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.link.DeepLink
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FamilyInviteLinkViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<DeepLink>>() {
    override fun initState(): APIResponse<DeepLink> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val familyId = arguments.get("familyId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            val familyLink = restAPI.getLinkApi().createFamilyLink(familyId = familyId)
            setState(familyLink.wrapToAPIResponse())
        }
    }

}