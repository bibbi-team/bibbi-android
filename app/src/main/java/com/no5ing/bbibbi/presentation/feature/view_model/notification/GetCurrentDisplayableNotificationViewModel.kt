package com.no5ing.bbibbi.presentation.feature.view_model.notification

import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.notification.NotificationModel
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.no5ing.bbibbi.util.parallelMap
import com.skydoves.sandwich.suspendMapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class GetCurrentDisplayableNotificationViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<APIResponse<List<NotificationModel>>>() {
    override fun initState(): APIResponse<List<NotificationModel>> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val meResult = restAPI.getNotificationApi().getDisplayableNotifications()
                .suspendMapSuccess {
                    parallelMap(Dispatchers.IO) {  notification ->
                        val member = runCatching { memberCacheProvider.getMember(notification.senderMemberId) }.getOrNull()
                        notification.copy(shouldDisplayAsBirthday = member?.isBirthdayToday ?: false)
                    }
                }
            val apiResult = meResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}