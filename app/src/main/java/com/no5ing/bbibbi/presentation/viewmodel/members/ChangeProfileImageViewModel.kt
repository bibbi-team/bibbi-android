package com.no5ing.bbibbi.presentation.viewmodel.members

import android.content.Context
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeProfileImageRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.fileFromContentUriStr
import com.no5ing.bbibbi.util.uploadImage
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltViewModel
class ChangeProfileImageViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val context: Context,
    private val client: OkHttpClient,
) : BaseViewModel<APIResponse<Member>>() {
    override fun initState(): APIResponse<Member> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val imageUri = arguments.get("imageUri") ?: throw RuntimeException()
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            setState(loading())
            val file = fileFromContentUriStr(context, imageUri)
            restAPI.getMemberApi().getUploadImageRequest(
                ImageUploadRequest(
                    imageName = file.name
                )
            ).suspendOnSuccess {
                val uploadedUrl = client.uploadImage(
                    targetFile = file,
                    targetUrl = data.url
                )
                if (uploadedUrl != null) {
                    val result = restAPI.getMemberApi().changeProfileImage(
                        memberId = memberId,
                        body = ChangeProfileImageRequest(
                            profileImageUrl = uploadedUrl,
                        )
                    ).wrapToAPIResponse()
                    setState(result)
                } else {
                    setState(APIResponse.unknownError())
                }
            }.suspendOnError {
                setState(APIResponse.errorOfOther(this))
            }
        }
    }

}