package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.FamilySummary
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class GetFamilySummaryViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<FamilySummary>>() {
    override fun initState(): APIResponse<FamilySummary> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val yearMonth = arguments.get("yearMonth") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            val result = restAPI.getPostApi().getFamilySummary(yearMonth)
            setState(result.wrapToAPIResponse())
        }
    }

}