package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.view.main.family.FamilyPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.SettingHomePageController.goSettingHomePage

object FamilyListPageController : NavigationDestination(
    route = mainFamilyPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        FamilyPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapFamily = {
                navController.goProfilePage(it.memberId)
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
                navController.goSettingHomePage()
            }
        )
    }

    fun NavHostController.goFamilyListPage() {
        navigate(FamilyListPageController)
    }
}
