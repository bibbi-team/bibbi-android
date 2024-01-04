package com.no5ing.bbibbi.presentation.viewmodel.family

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.family.FamilyInviteLinkUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.retrofit.body
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyInviteLinkViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<FamilyInviteLinkUiState>() {
    override fun initState(): FamilyInviteLinkUiState {
        return FamilyInviteLinkUiState(
            url = "Loading.."
        )
    }

    override fun invoke(arguments: Arguments) {
        viewModelScope.launch(Dispatchers.IO) {
            val familyId = localDataStorage.getMe()?.familyId ?: throw RuntimeException()
            val familyLink = restAPI.getLinkApi().createFamilyLink(familyId = familyId)
            familyLink.suspendOnSuccess {
                setState(
                    FamilyInviteLinkUiState(
                        url = body.url
                    )
                )
            }.suspendOnFailure {
                setState(
                    FamilyInviteLinkUiState(
                        url = "URL Error.."
                    )
                )
            }
        }
    }

}