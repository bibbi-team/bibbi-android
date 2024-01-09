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
) : BaseViewModel<FamilyInviteLinkUiState>() {
    override fun initState(): FamilyInviteLinkUiState {
        return FamilyInviteLinkUiState(
            url = "Loading.."
        )
    }

    override fun invoke(arguments: Arguments) {
        val familyId = arguments.get("familyId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
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