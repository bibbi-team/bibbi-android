package com.no5ing.bbibbi.presentation.ui.navigation.destination

import com.no5ing.bbibbi.presentation.ui.feature.setting.change_nickname.ChangeNicknamePage
import com.no5ing.bbibbi.presentation.ui.feature.setting.home.SettingHomePage
import com.no5ing.bbibbi.util.forceRestart

object SettingDestination : NavigationDestination(
    route = settingHomePageRoute,
    content = { navController, _ ->
        SettingHomePage(
            onDispose = {
                navController.popBackStack()
            },
            onLogout = {
                navController.context.forceRestart()
            },
            onQuitCompleted = {
                navController.context.forceRestart()
            }
        )
    }
)

object ChangeNicknameDestination : NavigationDestination(
    route = settingNickNameRoute,
    content = { navController, _ ->
        ChangeNicknamePage(
            onDispose = {
                navController.popBackStack()
            }
        )
    }
)

