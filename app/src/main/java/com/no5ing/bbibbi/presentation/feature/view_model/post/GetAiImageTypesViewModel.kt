package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.ArrayResponse
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.AIPostType
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class GetAiImageTypesViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<ArrayResponse<AIPostType>>>() {

    override fun initState(): APIResponse<ArrayResponse<AIPostType>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val result = restAPI.getPostApi().getAiImageTypes()
            setState(result.wrapToAPIResponse())
        }
    }
}
