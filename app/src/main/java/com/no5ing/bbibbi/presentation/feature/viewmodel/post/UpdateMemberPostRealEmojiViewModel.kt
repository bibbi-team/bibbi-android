package com.no5ing.bbibbi.presentation.feature.viewmodel.post

import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.CreateMemberRealEmojiRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.UpdateMemberRealEmojiRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.uploadImage
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateMemberPostRealEmojiViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
    private val client: OkHttpClient,
) : BaseViewModel<APIResponse<MemberRealEmoji>>() {
    override fun initState(): APIResponse<MemberRealEmoji> {
        return APIResponse.idle()
    }

    @OptIn(ExperimentalGetImage::class)
    override fun invoke(arguments: Arguments) {
        val emojiType = arguments.get("emojiType") ?: throw RuntimeException()
        val image = arguments.getObject<Uri?>("image") ?: throw RuntimeException()
        val prevEmojiKey = arguments.get("prevEmojiKey")
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            setState(APIResponse.loading())
            val file = File(image.path!!)
            restAPI.getMemberApi().getRealEmojiImageRequest(
                memberId = sessionModule.sessionState.value.memberId,
                ImageUploadRequest(
                    "${System.currentTimeMillis()}.jpg"
                )
            ).suspendOnSuccess {
                val uploadResult = client.uploadImage(
                    targetFile = file,
                    targetUrl = data.url
                )
                if (uploadResult == null) {
                    setState(APIResponse.unknownError())
                    return@suspendOnSuccess
                }
                val result = if (prevEmojiKey == null) {
                    restAPI.getMemberApi().createMemberRealEmoji(
                        memberId = sessionModule.sessionState.value.memberId,
                        body = CreateMemberRealEmojiRequest(
                            type = emojiType,
                            imageUrl = uploadResult
                        )
                    ).wrapToAPIResponse()
                } else {
                    restAPI.getMemberApi().updateMemberRealEmoji(
                        memberId = sessionModule.sessionState.value.memberId,
                        realEmojiId = prevEmojiKey,
                        body = UpdateMemberRealEmojiRequest(
                            imageUrl = uploadResult
                        )
                    ).wrapToAPIResponse()
                }
                setState(result)
            }.suspendOnError {
                setState(APIResponse.errorOfOther(otherResponse = this))
            }
        }
    }

}