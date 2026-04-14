package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.family_studio_upload.FamilyStudioUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination

object FamilyStudioUploadPageController : NavigationDestination(
    route = mainFamilyStudioUploadPageRoute,
    arguments = listOf(navArgument("aiPostType") {}),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val aiPostType = backStackEntry.arguments?.getString("aiPostType") ?: ""
        val imageCaptureState = backStackEntry.savedStateHandle
            .getLiveData<Uri?>("imageUrl")
            .observeAsState()
        FamilyStudioUploadPage(
            aiPostType = aiPostType,
            imageUrl = imageCaptureState,
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goFamilyStudioUploadPage(aiPostType: String) {
        navigate(FamilyStudioUploadPageController, params = listOf("aiPostType" to aiPostType))
    }
}
