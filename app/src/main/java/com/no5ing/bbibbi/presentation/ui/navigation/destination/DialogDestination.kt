package com.no5ing.bbibbi.presentation.ui.navigation.destination

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

abstract class DialogDestination(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavHostController, NavBackStackEntry) -> Unit,
) : NavigationDestination(route, arguments, null, content)

