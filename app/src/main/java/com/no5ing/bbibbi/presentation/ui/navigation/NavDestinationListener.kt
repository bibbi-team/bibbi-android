package com.no5ing.bbibbi.presentation.ui.navigation

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class NavDestinationListener(
    private val context: Context,
): NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val routes = controller.currentBackStack.value.joinToString("->") {
            it.destination.route ?: "START"
        }
        val routeString = "${routes}->${destination.route ?: "END"}"

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, destination.route)
        FirebaseAnalytics
            .getInstance(context)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)

        Timber.d("[NavRoute] $routeString")
    }
}