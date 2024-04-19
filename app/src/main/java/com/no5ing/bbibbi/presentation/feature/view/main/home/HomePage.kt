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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BackToExitHandler
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.gapUntilNext
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HomePage(
    mainPageViewModel: MainPageViewModel = hiltViewModel(),
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) },
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
    onTapProfile: (String) -> Unit = {},
    onTapContent: (String) -> Unit = {},
    onTapUpload: () -> Unit = {},
    onTapInvite: () -> Unit = {},
    onUnsavedPost: (Uri) -> Unit = {},
    onTapPick: (MainPageTopBarModel) -> Unit = {},
) {
    val mainPageState = mainPageViewModel.uiState.collectAsState()
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

    if (mainPageViewModel.shouldDisplayWidgetPopup) {
        mainPageViewModel.shouldDisplayWidgetPopup = false
        TryWidgetPopup()
    }
    BackToExitHandler()
    LaunchedEffect(Unit) {
        val tempUri = mainPageViewModel.getAndDeleteTemporaryUri()
        if (tempUri != null) {
            unsavedDialogUri.value = tempUri
            unsavedDialogEnabled.value = true
        }
        mainPageViewModel.invoke(Arguments())
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
                    mainPageState = mainPageViewModel.uiState,
                    postViewTypeState = postViewTypeState,
                    onTapContent = onTapContent,
                    onTapProfile = onTapProfile,
                    onTapInvite = onTapInvite,
                    onTapPick = onTapPick,
                    onRefresh = {
                        mainPageViewModel.invoke(Arguments())
                    },
                    deferredPickStateSet = mainPageViewModel.deferredPickMembersSet
                )
            }
            HomePageUploadButton(
                onTap = onTapUpload,
                isLoading = !mainPageState.value.isReady(),
                isUploadAbleTime = remember { gapUntilNext() > 0 },
                isAlreadyUploaded = !mainPageState.value.isReady() ||
                        mainPageState.value.data.isMeUploadedToday
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
                    mainPageState = MutableStateFlow(APIResponse.idle()),
                    deferredPickStateSet = MutableStateFlow(emptySet())
                )
            }
            HomePageUploadButton()
        }

    }
}

