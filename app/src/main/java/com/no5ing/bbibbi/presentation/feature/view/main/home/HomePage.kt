package com.no5ing.bbibbi.presentation.feature.view.main.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.state.main.home.HomePageContentState
import com.no5ing.bbibbi.presentation.feature.state.main.home.HomePageState
import com.no5ing.bbibbi.presentation.feature.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.feature.state.main.home.rememberHomePageContentState
import com.no5ing.bbibbi.presentation.feature.state.main.home.rememberHomePageState
import com.no5ing.bbibbi.presentation.feature.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.component.BackToExitHandler
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMembersViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.DailyFamilyTopViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.IsMeUploadedTodayViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.MainPostFeedViewModel
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun HomePage(
    homePageState: HomePageState = rememberHomePageState(),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    isMeUploadedTodayViewModel: IsMeUploadedTodayViewModel = hiltViewModel(),
    familyPostsViewModel: MainPostFeedViewModel = hiltViewModel(),
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    homePageContentState: HomePageContentState = rememberHomePageContentState(
        uiState = familyPostsViewModel.uiState
    ),
    familyPostTopViewModel: DailyFamilyTopViewModel = hiltViewModel(),
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(
        uiState = familyMembersViewModel.uiState,
        meState = retrieveMeViewModel.uiState,
        topState = familyPostTopViewModel.uiState,
    ),
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
    onTapProfile: (Member) -> Unit = {},
    onTapContent: (Post) -> Unit = {},
    onTapUpload: () -> Unit = {},
    onTapInvite: () -> Unit = {},
    onUnsavedPost: (Uri) -> Unit = {},
) {
    val memberId = LocalSessionState.current.memberId
    val meUploadedState = isMeUploadedTodayViewModel.uiState.collectAsState()
    //val familyMembersState = familyMembersViewModel.uiState.collectAsState()

    val unsavedDialogUri = remember { mutableStateOf<Uri?>(null) }
    val unsavedDialogEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        enabledState = unsavedDialogEnabled,
        title = stringResource(id = R.string.unsaved_post_dialog_title),
        description = stringResource(id = R.string.unsaved_post_dialog_message),
        confirmRequest = {
            unsavedDialogEnabled.value = false
            onUnsavedPost(unsavedDialogUri.value!!)
        }
    )
    BackToExitHandler()
    LaunchedEffect(Unit) {
        isMeUploadedTodayViewModel.invoke(Arguments(arguments = mapOf("memberId" to memberId)))
        val tempUri = retrieveMeViewModel.getAndDeleteTemporaryUri()
        if (tempUri != null) {
            unsavedDialogUri.value = tempUri
            unsavedDialogEnabled.value = true
        }
    }


    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(MaterialTheme.bbibbiScheme.backgroundPrimary)
            ) {
                HomePageTopBar(
                    onTapLeft = onTapLeft,
                    onTapRight = onTapRight
                )
                HomePageContent(
                    familyMembersViewModel = familyMembersViewModel,
                    familyPostsViewModel = familyPostsViewModel,
                    familyPostTopViewModel = familyPostTopViewModel,
                    homePageContentState = homePageContentState,
                    retrieveMeViewModel = retrieveMeViewModel,
                    storyBarState = storyBarState,
                    onTapContent = onTapContent,
                    onTapProfile = onTapProfile,
                    onTapInvite = onTapInvite,
                )
            }
            HomePageUploadButton(
                onTap = onTapUpload,
                isLoading = !meUploadedState.value.isReady(),
                isUploadAbleTime = remember { gapUntilNext() > 0 },
                isAlreadyUploaded = !meUploadedState.value.isReady() ||
                        meUploadedState.value.data
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Preview",
    showSystemUi = true
)
@Composable
fun HomePagePreview() {
    BbibbiTheme {
        HomePage(
            retrieveMeViewModel = hiltViewModel(),
            isMeUploadedTodayViewModel = hiltViewModel(),
        )
    }
}
