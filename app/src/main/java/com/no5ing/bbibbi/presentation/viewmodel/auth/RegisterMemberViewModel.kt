package com.no5ing.bbibbi.presentation.viewmodel.auth

import android.content.Context
import android.net.Uri
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.errorOfOther
import com.no5ing.bbibbi.data.model.APIResponse.Companion.onFailed
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.auth.RegisterRequest
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.uploadImage
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterMemberViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val context: Context,
    private val client: OkHttpClient,
    private val sessionModule: SessionModule,
) : BaseViewModel<APIResponse<Member>>() {
    override fun initState(): APIResponse<Member> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val imageUri = arguments.get("imageUri")
        val memberName = arguments.get("memberName") ?: throw RuntimeException()
        val memberBirth = arguments.get("dayOfBirth") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            setState(APIResponse.loading())
            val uploadedImageUrl = if (imageUri != null) {
                val file = File(Uri.parse(imageUri).path!!)
                var uploadedUrl: String? = null
                restAPI.getMemberApi().getUploadImageRequest(
                    ImageUploadRequest(
                        imageName = file.name
                    )
                ).onSuccess {
                    uploadedUrl = client.uploadImage(
                        targetFile = file,
                        targetUrl = data.url
                    )
                }
                uploadedUrl
            } else null
            restAPI.getAuthApi().register(
                RegisterRequest(
                    memberName = memberName,
                    dayOfBirth = memberBirth,
                    profileImgUrl = uploadedImageUrl,
                )
            ).suspendOnSuccess {
                val authToken = data
                sessionModule.onLoginWithTemporaryCredentials(
                    newTokenPair = authToken,
                )
                val res = restAPI.getMemberApi().getMeInfo().suspendOnSuccess {
                    sessionModule.onLoginWithCredentials(
                        newTokenPair = authToken,
                        member = data,
                    )
                }
                setState(res.wrapToAPIResponse())
            }.onError {
                setState(errorOfOther(this))
            }.onFailed {
                setState(APIResponse.unknownError())
            }
        }
    }
}