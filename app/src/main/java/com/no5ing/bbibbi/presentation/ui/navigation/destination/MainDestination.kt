package com.no5ing.bbibbi.presentation.ui.navigation.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.ui.feature.main.calendar.MainCalendarPage
import com.no5ing.bbibbi.presentation.ui.feature.main.calendar.detail.CalendarDetailPage
import com.no5ing.bbibbi.presentation.ui.feature.main.family.FamilyPage
import com.no5ing.bbibbi.presentation.ui.feature.main.home.HomePage
import com.no5ing.bbibbi.presentation.ui.feature.main.profile.ProfilePage
import timber.log.Timber
import java.time.LocalDate

object MainHomeDestination : NavigationDestination(
    route = mainHomePageRoute,
    content = { navController, _ ->
        HomePage(
            onTapLeft = {
                navController.navigate(MainFamilyDestination)
            },
            onTapRight = {
                navController.navigate(MainCalendarDestination)
            },
            onTapProfile = {
                navController.navigate(
                    destination = MainProfileDestination,
                    path = it.memberId
                )
            },
            onTapContent = {
                navController.navigate(
                    destination = PostViewDestination,
                    path = it.postId
                )
            },
            onTapUpload = {
                navController.navigate(PostUploadDestination)
                navController.dialog(
                    destination = CameraDialogDestination,
                )
            },
            onTapInvite = {
                navController.navigate(MainFamilyDestination)
            }

        )
    },
)

object MainProfileDestination : NavigationDestination(
    route = mainProfilePageRoute,
    pathVariable = navArgument("memberId") {},
    content = { navController, backStackEntry ->
        val imgState = remember { mutableStateOf<Uri?>(null) }
        val imgUrl = backStackEntry.savedStateHandle.remove<Uri?>("imageUrl")
        if (imgUrl != null) {
            Timber.d("Img state updated")
            imgState.value = imgUrl
        }
        ProfilePage(
            memberId = backStackEntry.arguments?.getString("memberId")
                ?: "UNKNOWN",
            onDispose = {
                navController.popBackStack()
            },
            onTapSetting = {
                navController.navigate(SettingDestination)
            },
            onTapPost = {
                navController.navigate(
                    destination = PostViewDestination,
                    path = it.postId
                )
            },
            onTapChangeNickname = {
                navController.navigate(
                    ChangeNicknameDestination
                )
            },
            onTapCamera = {
                navController.dialog(
                    destination = CameraDialogDestination,
                )
            },
            changeableUriState = imgState,
        )
    },
)

object MainFamilyDestination : NavigationDestination(
    route = mainFamilyPageRoute,
    // pathVariable = navArgument("familyId") {},
    content = { navController, _ ->
        FamilyPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapFamily = {
                navController.navigate(
                    destination = MainProfileDestination,
                    path = it.memberId
                )
            },
            onTapShare = { url ->
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_SUBJECT, "초대하기")
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share URL")
                navController.context.startActivity(shareIntent)
            },
        )
    },
)

object MainCalendarDestination : NavigationDestination(
    route = mainCalendarPageRoute,
    content = { navController, _ ->
        MainCalendarPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapDay = {
                navController.navigate(
                    MainCalendarDetailDestination,
                    params = listOf(
                        "date" to it.toString()
                    )
                )
            }
        )
    },
)

object MainCalendarDetailDestination : NavigationDestination(
    route = mainCalendarDetailPageRoute,
    arguments = listOf(navArgument("date") { }),
    content = { navController, backStackEntry ->
        val dateStr = backStackEntry.arguments?.getString("date")
        val date = dateStr?.let(LocalDate::parse) ?: LocalDate.now()
        CalendarDetailPage(
            initialDay = date,
            onDispose = {
                navController.popBackStack()
            },
            onTapProfile = {
                navController.navigate(
                    destination = MainProfileDestination,
                    path = it.memberId
                )
            }
        )
    },
)