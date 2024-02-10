package com.no5ing.bbibbi.presentation.feature.view_controller.landing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view.landing.already_family_exists.AlreadyFamilyExistsView
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.JoinFamilyWithLinkPageController.goJoinFamilyWithLinkPage
import com.no5ing.bbibbi.presentation.feature.view_model.family.QuitFamilyViewModel


object AlreadyFamilyExistsPageController : NavigationDestination(
    route = landingAlreadyFamilyExistsRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val familyQuitViewModel: QuitFamilyViewModel = hiltViewModel()
        val familyQuitState by familyQuitViewModel.uiState.collectAsState()
        LaunchedEffect(familyQuitState) {
            if (familyQuitState.isReady()) {
                navController.popAll()
                navController.goJoinFamilyWithLinkPage()
            }
        }
        AlreadyFamilyExistsView(
            onTapDispose = {
                navController.popBackStack()
            },
            onTapQuitAndJoin = {
                if (familyQuitState.isIdle()) {
                    familyQuitViewModel.invoke(Arguments())
                }
            }
        )
    }

    fun NavHostController.goAlreadyFamilyExistsPage() {
        navigate(AlreadyFamilyExistsPageController)
    }
}


