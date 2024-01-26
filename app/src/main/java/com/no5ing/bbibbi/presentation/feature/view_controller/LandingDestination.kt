package com.no5ing.bbibbi.presentation.feature.view_controller

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.state.landing.login.LoginSucceedResult
import com.no5ing.bbibbi.presentation.feature.view.landing.already_family_exists.AlreadyFamilyExistsView
import com.no5ing.bbibbi.presentation.feature.view.landing.join_family.JoinFamilyPage
import com.no5ing.bbibbi.presentation.feature.view.landing.join_family_with_link.JoinFamilyWithLinkPage
import com.no5ing.bbibbi.presentation.feature.view.landing.login.LoginPage
import com.no5ing.bbibbi.presentation.feature.view.landing.onboarding.OnBoardingPage

object LandingLoginDestination : NavigationDestination(
    route = landingLoginPageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        LoginPage(
            onCompleted = { result ->
                when (result) {
                    LoginSucceedResult.PERMANENT_HAS_FAMILY -> {
                        //navController.navigate(RegisterNicknameDestination)
                        navController.popAll()
                        navController.navigate(LandingOnBoardingDestination)
                    }

                    LoginSucceedResult.PERMANENT_NO_FAMILY -> {
                        navController.navigate(LandingOnBoardingDestination)

                    }

                    LoginSucceedResult.TEMPORARY -> {
                        navController.navigate(RegisterNicknameDestination)
                    }
                }
            },
        )
    }
}

object LandingOnBoardingDestination : NavigationDestination(
    route = landingOnBoardingRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        OnBoardingPage(
            onAlreadyHaveFamily = {
                navController.popAll()
                navController.navigate(MainHomeDestination)
            },
            onFamilyNotExists = {
                navController.navigate(LandingJoinFamilyDestination)
            }
        )
    }
}


object LandingAlreadyFamilyExistsDestination : NavigationDestination(
    route = landingAlreadyFamilyExistsRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        AlreadyFamilyExistsView(
            onTapDispose = {
                navController.popBackStack()
            }
        )
    }
}

object LandingJoinFamilyDestination : NavigationDestination(
    route = landingJoinFamilyRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        JoinFamilyPage(
            onTapJoinWithLink = {
                navController.navigate(LandingJoinFamilyWithLinkDestination)
            },
            onFamilyCreationComplete = {
                navController.popAll()
                navController.navigate(MainHomeDestination)
            }
        )
    }
}

object LandingJoinFamilyWithLinkDestination : NavigationDestination(
    route = landingJoinFamilyWithLinkRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        JoinFamilyWithLinkPage(
            onDispose = {
                navController.popBackStack()
            },
            onJoinComplete = {
                navController.popAll()
                navController.navigate(MainHomeDestination)
            }
        )
    }
}