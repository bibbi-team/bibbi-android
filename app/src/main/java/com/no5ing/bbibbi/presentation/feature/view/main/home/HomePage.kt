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
import androidx.paging.PagingData
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BackToExitHandler
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMembersViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.DailyFamilyTopViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.IsMeUploadedTodayViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.MainPostFeedViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.gapUntilNext
import com.no5ing.bbibbi.util.todayAsString
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HomePage(
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    isMeUploadedTodayViewModel: IsMeUploadedTodayViewModel = hiltViewModel(),
    familyPostsViewModel: MainPostFeedViewModel = hiltViewModel(),
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    familyPostTopViewModel: DailyFamilyTopViewModel = hiltViewModel(),
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

        if (familyPostsViewModel.isInitialize()) {
            familyMembersViewModel.invoke(Arguments())
            retrieveMeViewModel.invoke(Arguments())
            familyPostTopViewModel.invoke(Arguments())// TODO
            familyPostsViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "date" to todayAsString(),
                    )
                )
            )
        } else {
            familyPostTopViewModel.invoke(Arguments())
            familyPostsViewModel.refresh()
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
                    contentState = familyPostsViewModel.uiState,
                    familyListState = familyMembersViewModel.uiState,
                    postTopState = familyPostTopViewModel.uiState,
                    meState = retrieveMeViewModel.uiState,
                    onTapContent = onTapContent,
                    onTapProfile = onTapProfile,
                    onTapInvite = onTapInvite,
                    onRefresh = {
                        familyPostTopViewModel.invoke(Arguments())
                        retrieveMeViewModel.invoke(Arguments())
                    }
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
    name = "HomePagePreview",
    showSystemUi = true
)
@Composable
fun HomePagePreview() {
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                HomePageTopBar()
                HomePageContent(
                    contentState = MutableStateFlow(PagingData.empty()),
                    familyListState = MutableStateFlow(PagingData.empty()),
                    postTopState = MutableStateFlow(emptyMap()),
                    meState = MutableStateFlow(APIResponse.success(Member.unknown()))
                )
            }
            HomePageUploadButton()
        }

    }
}

