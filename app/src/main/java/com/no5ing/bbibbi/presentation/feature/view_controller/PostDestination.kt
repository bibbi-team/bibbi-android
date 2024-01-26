package com.no5ing.bbibbi.presentation.feature.view_controller

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji.CreateRealEmojiPage
import com.no5ing.bbibbi.presentation.feature.view.main.post_upload.PostUploadPage
import com.no5ing.bbibbi.presentation.feature.view.main.post_view.PostViewPage

object PostViewDestination : NavigationDestination(
    route = postViewPageRoute,
    pathVariable = navArgument("postId") {},
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        PostViewPage(
            postId = backStackEntry.arguments?.getString("postId") ?: "UNKNOWN",
            onDispose = {
                navController.popBackStack()
            },
            onTapProfile = {
                navController.navigate(
                    destination = MainProfileDestination,
                    path = it.memberId
                )
            },
            onTapRealEmojiCreate = {
                navController.navigate(
                    destination = CreateRealEmojiDestination,
                    params = listOf(
                        "initialEmoji" to it
                    )
                )
            }
        )
    }
}

object PostUploadDestination : NavigationDestination(
    route = postUploadRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.savedStateHandle
            .getLiveData<Uri?>("imageUrl")
            .observeAsState()
        PostUploadPage(
            imageUrl = imageCaptureState,
            onDispose = {
                navController.popBackStack()
            },
        )
    }
}

object PostReUploadDestination : NavigationDestination(
    route = postReUploadRoute,
    arguments = listOf(navArgument("imageUrl") {}),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageCaptureState = backStackEntry.arguments?.getString("imageUrl")
        val uriState = remember { mutableStateOf(Uri.parse(imageCaptureState)) }
        PostUploadPage(
            imageUrl = uriState,
            isUnsaveMode = true,
            onDispose = {
                navController.popBackStack()
            },
        )
    }
}

object CreateRealEmojiDestination : NavigationDestination(
    arguments = listOf(navArgument("initialEmoji") {}),
    route = postCreateRealEmojiRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        CreateRealEmojiPage(
            initialEmoji = backStackEntry.arguments?.getString("initialEmoji")
                ?: throw RuntimeException(),
            onDispose = {
                navController.popBackStack()
            },
        )
    }
}
