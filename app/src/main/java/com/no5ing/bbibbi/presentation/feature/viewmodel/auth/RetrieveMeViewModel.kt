package com.no5ing.bbibbi.presentation.feature.viewmodel.auth

import android.net.Uri
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RetrieveMeViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Member>>() {
    override fun initState(): APIResponse<Member> {
        return APIResponse.idle()
    }

    fun getAndDeleteTemporaryUri(): Uri? {
        val uri = localDataStorage.getTemporaryUri()
        if (uri != null) {
            localDataStorage.clearTemporaryUri()
        }
        return uri?.let { Uri.parse(it) }
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val meResult = restAPI.getMemberApi().getMeInfo()
            val apiResult = meResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}