package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.feature.view.main.family.FamilyPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ChangeFamilyNamePageController.goChangeFamilyNamePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.SettingHomePageController.goSettingHomePage
import com.no5ing.bbibbi.util.getLinkIdFromUrl
import com.no5ing.bbibbi.util.localResources

object FamilyListPageController : NavigationDestination(
    route = mainFamilyPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val resources = localResources()
        FamilyPage(
            onDispose = {
                navController.popBackStack()
            },
            onTapFamily = {
                navController.goProfilePage(it.memberId)
            },
            onTapShare = { url ->
                val linkId = getLinkIdFromUrl(url)
                val payload = resources.getString(R.string.share_link_payload, url, linkId)
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_SUBJECT, "초대하기")
                    putExtra(Intent.EXTRA_TEXT, payload)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share URL")
                navController.context.startActivity(shareIntent)
            },
            onTapSetting = {
                navController.goSettingHomePage()
            },
            onTapFamilyNameChange = {
                navController.popBackStack()
                navController.goChangeFamilyNamePage()
            }
        )
    }

    fun NavHostController.goFamilyListPage() {
        navigate(FamilyListPageController)
    }
}
