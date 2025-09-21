package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.presentation.feature.view.main.family_studio.FamilyStudioImagePreviewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.util.toLocalizedDate

object FamilyStudioPreviewPageController : NavigationDestination(
    route = mainFamilyStudioPreviewPageRoute,
    arguments = listOf(navArgument("imageUrl") {}, navArgument("authorImageUrl") {}, navArgument("authorName") {}, navArgument("date") {}),
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
        val authorImageUrl = backStackEntry.arguments?.getString("authorImageUrl") ?: ""
        val authorName = backStackEntry.arguments?.getString("authorName") ?: ""
        val date = backStackEntry.arguments?.getString("date") ?: ""
        FamilyStudioImagePreviewPage(
            imageUrl = imageUrl,
            authorImageUrl = authorImageUrl,
            authorName = authorName,
            date = date,
            onDispose = {
                navController.popBackStack()
            },
        )
    }

    fun NavHostController.goFamilyImagePreviewPage(post: AIPost) {
        navigate(FamilyStudioPreviewPageController, params = listOf(
            "imageUrl" to post.imageUrl,
            "authorImageUrl" to (post.authorImageUrl?:""),
            "authorName" to (post.authorName ?: ""),
            "date" to toLocalizedDate(post.createdAt),
        ))
    }
}