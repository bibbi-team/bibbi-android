package com.no5ing.bbibbi.data.model.notification

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.settingHomePageRoute
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime
import kotlin.random.Random

@Parcelize
data class NotificationModel(
    val notificationId: String,
    val senderProfileImageUrl: String,
    val senderMemberId: String,
    val title: String,
    val content: String,
    val aosDeepLink: String,
    val createdAt: ZonedDateTime,
    val shouldDisplayAsBirthday: Boolean = false,
): Parcelable, BaseModel() {
    companion object {
        fun mock() = NotificationModel(
            notificationId = Random.nextInt().toString(),
            senderProfileImageUrl = "https://picsum.photos/200/300",
            senderMemberId = "",
            title = "테스트 노티 제목",
            content = "이 내용은 노티의 본문으로 들어갑니다..",
            aosDeepLink = settingHomePageRoute,
            createdAt = ZonedDateTime.now().minusMinutes(Random.nextLong(1, 60 * 24 * 7))
        )
    }
}
