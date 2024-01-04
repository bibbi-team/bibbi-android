package com.no5ing.bbibbi.presentation.ui.navigation.destination

import android.net.Uri
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.navArgument
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
