package com.no5ing.bbibbi.presentation.ui.navigation.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
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
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
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
                navController.navigate(
                    destination = CameraViewDestination,
                )
            },
            onTapInvite = {
                navController.navigate(MainFamilyDestination)
            },
            onUnsavedPost = {
                navController.navigate(
                    PostReUploadDestination,
                    params = listOf("imageUrl" to it.toString())
                )
            }
        )
    }
}

object MainProfileDestination : NavigationDestination(
    route = mainProfilePageRoute,
    pathVariable = navArgument("memberId") {}
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imgState = remember { mutableStateOf<Uri?>(null) }
        val imgUrl = backStackEntry.savedStateHandle.remove<Uri?>("imageUrl")
        if (imgUrl != null) {
            Timber.d("[ProfilePage] Img state updated")
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
                navController.navigate(
                    destination = CameraViewDestination,
                )
            },
            changeableUriState = imgState,
        )
    }
}

object MainFamilyDestination : NavigationDestination(
    route = mainFamilyPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
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
            onTapSetting = {
                navController.navigate(SettingDestination)
            }
        )
    }
}

object MainCalendarDestination : NavigationDestination(
    route = mainCalendarPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
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
    }
}

object MainCalendarDetailDestination : NavigationDestination(
    route = mainCalendarDetailPageRoute,
    arguments = listOf(navArgument("date") { }),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
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
            },
            onTapRealEmojiCreate = {
                navController.navigate(
                    destination = CreateRealEmojiDestination,
                    params = listOf(
                        "initialEmoji" to it
                    )
                )
            }
        )
    }
}