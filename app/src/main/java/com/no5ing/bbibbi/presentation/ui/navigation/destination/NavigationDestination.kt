package com.no5ing.bbibbi.presentation.ui.navigation.destination

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import timber.log.Timber

abstract class NavigationDestination(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val pathVariable: NamedNavArgument? = null,
    @Stable val content: @Composable (NavHostController, NavBackStackEntry) -> Unit,
) {
    val combinedArguments =
        arguments + if (pathVariable != null) listOf(pathVariable) else emptyList()

    private val routeWithPath =
        "${route}${if (pathVariable != null) "/{${pathVariable.name}}" else ""}"
    val routeWithQuery = if (arguments.isEmpty()) routeWithPath
    else "$routeWithPath?${arguments.joinToString("&") { "${it.name}={${it.name}}" }}"

    companion object {
        internal const val landingPageRoute = "landing"
        internal const val landingLoginPageRoute = "landing/login"
        internal const val landingOnBoardingRoute = "landing/onboarding"
        internal const val landingJoinFamilyRoute = "landing/join-family"
        internal const val landingJoinFamilyWithLinkRoute = "landing/join-family-with-link"
        internal const val landingAlreadyFamilyExistsRoute = "landing/already-family-exists"

        internal const val registerPageRoute = "register"
        internal const val registerNickNameRoute = "register/nickname"
        internal const val registerDayOfBirthRoute = "register/day-of-birth"
        internal const val registerProfileImageRoute = "register/profile-image"

        internal const val mainPageRoute = "main"
        internal const val mainHomePageRoute = "main/home"
        internal const val mainFamilyPageRoute = "main/family"
        internal const val mainCalendarPageRoute = "main/calendar"
        internal const val mainCalendarDetailPageRoute = "main/calendar/detail"
        internal const val mainProfilePageRoute = "main/profile"

        internal const val postPageRoute = "post"
        internal const val postViewPageRoute = "post/view"
        internal const val postUploadRoute = "post/upload"
        internal const val postCreateRealEmojiRoute = "post/create-real-emoji"

        internal const val settingPageRoute = "setting"
        internal const val settingHomePageRoute = "setting/home"
        internal const val settingNickNameRoute = "setting/nickname"
        internal const val settingWebViewPageRoute = "setting/webview"
        internal const val settingQuitPageRoute = "setting/quit"
        internal const val cameraViewRoute = "common/camera"


        @OptIn(ExperimentalAnimationApi::class)
        fun NavGraphBuilder.composable(
            controller: NavHostController,
            destination: NavigationDestination,
            enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
            exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
            popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
            popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
        ) = composable(
            route = destination.routeWithQuery,
            arguments = destination.combinedArguments,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ) {
            destination.content(controller, it)
        }

        @OptIn(ExperimentalAnimationApi::class)
        fun NavGraphBuilder.dialog(
            controller: NavHostController,
            destination: NavigationDestination,
        ) = composable(
            route = destination.routeWithQuery,
            arguments = destination.arguments,
        ) {
            destination.content(controller, it)
        }

        fun NavHostController.popAll() {
            while (popBackStack()) {
            }
        }

        fun NavHostController.navigate(
            destination: NavigationDestination,
            path: String? = null,
            params: List<Pair<String, String>> = emptyList(),
        ) {
            val routeWithPath = "${destination.route}${if (path != null) "/${path}" else ""}"
            val targetRoute = if (params.isEmpty())
                routeWithPath
            else
                "${routeWithPath}?${params.joinToString("&") { "${it.first}=${it.second}" }}"
            val node = NavDeepLinkRequest
                .Builder
                .fromUri(NavDestination.createRoute(targetRoute).toUri()).build()
            val deepLinkMatch = graph.matchDeepLink(node) ?: throw RuntimeException()

            val priorStack = currentBackStack.value.firstOrNull {
                deepLinkMatch.hasMatchingArgs(it.arguments)
                        && it.destination.route == deepLinkMatch.destination.route
            }
            if (priorStack != null) {
                Timber.d("[NavRouter] Going back prior opened stack..")
                popBackStack(
                    destinationId = priorStack.destination.id,
                    inclusive = false,
                )
                return
            }


            navigate(
                targetRoute,
            ) {
                restoreState = true
            }
        }

        @Stable
        fun NavHostController.dialog(
            destination: DialogDestination,
            params: List<Pair<String, String>> = emptyList(),
        ) {
            navigate(
                if (params.isEmpty())
                    destination.route
                else
                    "${destination.route}?${params.joinToString("&") { "${it.first}=${it.second}" }}",
            )
        }

    }
}