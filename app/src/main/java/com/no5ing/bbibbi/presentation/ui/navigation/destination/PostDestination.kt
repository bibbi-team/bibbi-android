package com.no5ing.bbibbi.presentation.ui.navigation.destination

import android.net.Uri
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.navArgument
import com.no5ing.bbibbi.presentation.ui.feature.post.create_real_emoji.CreateRealEmojiPage
import com.no5ing.bbibbi.presentation.ui.feature.post.upload.PostUploadPage
import com.no5ing.bbibbi.presentation.ui.feature.post.view.PostViewPage

object PostViewDestination : NavigationDestination(
    route = postViewPageRoute,
    pathVariable = navArgument("postId") {},
    content = { navController, entry ->
        PostViewPage(
            postId = entry.arguments?.getString("postId") ?: "UNKNOWN",
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
)

object PostUploadDestination : NavigationDestination(
    route = postUploadRoute,
    content = { navController, backStackEntry ->
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
)

object PostReUploadDestination : NavigationDestination(
    route = postReUploadRoute,
    arguments = listOf(navArgument("imageUrl") {}),
    content = { navController, backStackEntry ->
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
)

object CreateRealEmojiDestination : NavigationDestination(
    arguments = listOf(navArgument("initialEmoji") {}),
    route = postCreateRealEmojiRoute,
    content = { navController, backStackEntry ->
        CreateRealEmojiPage(
            initialEmoji = backStackEntry.arguments?.getString("initialEmoji")
                ?: throw RuntimeException(),
            onDispose = {
                navController.popBackStack()
            },
        )
    }
)
