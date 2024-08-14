package com.no5ing.bbibbi.data.model.view

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.ZonedDateTime

@Parcelize
data class MainPageModel(
    val topBarElements: List<MainPageTopBarModel>,
    val isMissionUnlocked: Boolean,
    val isMeSurvivalUploadedToday: Boolean,
    val isMeMissionUploadedToday: Boolean,
    val survivalFeeds: List<MainPageFeedModel>,
    val missionFeeds: List<MainPageFeedModel>,
    val pickers: List<MainPagePickerModel>,
    val leftUploadCountUntilMissionUnlock: Int,
    val dailyMissionContent: String,
) : Parcelable, BaseModel()

@Parcelize
data class NightMainPageModel(
    val topBarElements: List<MainPageTopBarModel>,
    val familyMemberMonthlyRanking: MainPageMonthlyRankingModel,
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
    val imageUrl: String?,
    val displayName: String?,
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
    val familyName: String?,
) : Parcelable

@Parcelize
data class MainPageMonthlyRankingModel(
    val month: Int,
    val firstRanker: MainPageMonthlyRankerModel?,
    val secondRanker: MainPageMonthlyRankerModel?,
    val thirdRanker: MainPageMonthlyRankerModel?,
    val mostRecentSurvivalPostDate: LocalDate?,
) : Parcelable {
    fun isAllRankersNull(): Boolean {
        return firstRanker == null && secondRanker == null && thirdRanker == null
    }
}

@Parcelize
data class MainPageMonthlyRankerModel(
    val profileImageUrl: String?,
    val name: String,
    val survivalCount: Int,
) : Parcelable