package com.no5ing.bbibbi.presentation.viewmodel.auth

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RetrieveMeViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Member>>() {
    override fun initState(): APIResponse<Member> {
        return APIResponse.loading()
    }

    override fun invoke(arguments: Arguments) {
        viewModelScope.launch(Dispatchers.IO) {
            val meResult = restAPI.getMemberApi().getMeInfo()
            val apiResult = meResult.wrapToAPIResponse()
            if (apiResult.status is APIResponse.Status.SUCCESS) {
                localDataStorage.setMe(apiResult.data)
            }
            setState(apiResult)
        }
    }


}