package com.no5ing.bbibbi.presentation.ui.navigation.destination

import com.no5ing.bbibbi.presentation.state.landing.login.LoginSucceedResult
import com.no5ing.bbibbi.presentation.ui.feature.landing.already_family_exists.AlreadyFamilyExistsView
import com.no5ing.bbibbi.presentation.ui.feature.landing.join_family.JoinFamilyPage
import com.no5ing.bbibbi.presentation.ui.feature.landing.join_family_with_link.JoinFamilyWithLinkPage
import com.no5ing.bbibbi.presentation.ui.feature.landing.login.LoginPage
import com.no5ing.bbibbi.presentation.ui.feature.landing.onboarding.OnBoardingPage

object LandingLoginDestination : NavigationDestination(
    route = landingLoginPageRoute,
    content = { navController, _ ->
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
)

object LandingOnBoardingDestination : NavigationDestination(
    route = landingOnBoardingRoute,
    content = { navController, _ ->
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
)


object LandingAlreadyFamilyExistsDestination : NavigationDestination(
    route = landingAlreadyFamilyExistsRoute,
    content = { navController, _ ->
        AlreadyFamilyExistsView(
            onTapDispose = {
                navController.popBackStack()
            }
        )
    }
)

object LandingJoinFamilyDestination : NavigationDestination(
    route = landingJoinFamilyRoute,
    content = { navController, _ ->
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
)

object LandingJoinFamilyWithLinkDestination : NavigationDestination(
    route = landingJoinFamilyWithLinkRoute,
    content = { navController, _ ->
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
)