package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.presentation.feature.view.main.family_studio.FamilyStudioPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyStudioCameraPageController.goFamilyStudioCameraPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyStudioPreviewPageController.goFamilyImagePreviewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyStudioUploadPageController.goFamilyStudioUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.HomePageController.goHomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.WebViewPageController.goWebViewPage
import com.no5ing.bbibbi.presentation.feature.view_model.post.GetAiImageCountViewModel

object FamilyStudioPageController: NavigationDestination(
    route = mainFamilyStudioPageRoute
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val aiImageCountViewModel = hiltViewModel<GetAiImageCountViewModel>()
        val dialogState = remember { mutableStateOf(false) }
        if (aiImageCountViewModel.shouldShowTermDialog()) {
            dialogState.value = true
        }
        FamilyStudioPage(
            isTermDialogEnabled = dialogState,
            aiImageCountViewModel = aiImageCountViewModel,
            onDispose = {
                navController.popBackStack()
            },
            onTapCreateImage = {
                navController.goFamilyStudioUploadPage()
                navController.goFamilyStudioCameraPage()
            },
            onTapAiPost = {
                navController.goFamilyImagePreviewPage(it)
            },
            onDisagreeTerm = {
                navController.goHomePage()
            },
            onAgreeTerm = {
                aiImageCountViewModel.hideTermDialog()
                dialogState.value = false
            },
            onClickTerm = {
                navController.goWebViewPage(BuildConfig.aiTermUrl)
            }
        )
    }

    fun NavHostController.goFamilyStudioPage() {
        navigate(FamilyStudioPageController)
    }
}