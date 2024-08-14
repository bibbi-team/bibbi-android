package com.no5ing.bbibbi.presentation.feature.view_model.family

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FamilyInfoViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Family>>() {
    override fun initState(): APIResponse<Family> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val familyLink = restAPI.getFamilyApi().getMyFamily()
            setState(familyLink.wrapToAPIResponse())
        }
    }

}