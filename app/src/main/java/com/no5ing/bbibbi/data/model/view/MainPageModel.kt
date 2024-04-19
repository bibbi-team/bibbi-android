package com.no5ing.bbibbi.data.model.view

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class MainPageModel(
    val topBarElements: List<MainPageTopBarModel>,
    val isMissionUnlocked: Boolean,
    val isMeUploadedToday: Boolean,
    val survivalFeeds: List<MainPageFeedModel>,
    val missionFeeds: List<MainPageFeedModel>,
    val pickers: List<MainPagePickerModel>
) : Parcelable, BaseModel()

@Parcelize
data class MainPageFeedModel(
    val postId: String,
    val imageUrl: String,
    val authorName: String,
    val createdAt: ZonedDateTime,
) : Parcelable

@Parcelize
data class MainPagePickerModel(
    val memberId: String,
    val imageUrl: String,
    val displayName: String,
) : Parcelable

@Parcelize
data class MainPageTopBarModel(
    val memberId: String,
    val imageUrl: String?,
    val noImageLetter: String,
    val displayName: String,
    val displayRank: Int?,
    val shouldShowBirthdayMark: Boolean,
    val shouldShowPickIcon: Boolean,
) : Parcelable