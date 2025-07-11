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
import androidx.compose.runtime.getValue
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
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageNightViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.gapUntilNext
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@Composable
fun HomePage(
    mainPageViewModel: MainPageViewModel = hiltViewModel(),
    mainPageNightViewModel: MainPageNightViewModel = hiltViewModel(),
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) },
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
    onTapAlarm: () -> Unit = {},
    onTapProfile: (String) -> Unit = {},
    onTapContent: (String) -> Unit = {},
    onTapUpload: () -> Unit = {},
    onTapMissionUpload: () -> Unit = {},
    onTapInvite: () -> Unit = {},
    onUnsavedPost: (Uri) -> Unit = {},
    onTapViewPost: (LocalDate) -> Unit = {},
    onTapPick: (MainPageTopBarModel) -> Unit = {},
    onTapNight: () -> Unit = {},
) {
    val postViewType by postViewTypeState
    val mainPageState = mainPageViewModel.uiState.collectAsState()
    val mainPageNightState = mainPageNightViewModel.uiState.collectAsState()
    val unsavedDialogUri = remember { mutableStateOf<Uri?>(null) }
    val unsavedDialogEnabled = remember { mutableStateOf(false) }
    val isDayTime = gapUntilNext() > 0
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
        if (isDayTime) {
            mainPageViewModel.invoke(Arguments())
        } else {
            mainPageViewModel.invoke(Arguments())
            mainPageNightViewModel.invoke(Arguments())
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
                if (isDayTime) {
                    HomePageTopBar(
                        onTapLeft = {
                            mainPageViewModel.hideShowFamilyNewIcon()
                            onTapLeft()
                        },
                        onTapAlarm = onTapAlarm,
                        onTapRight = onTapRight,
                        isNewIconEnabled = mainPageViewModel.shouldShowFamilyNewIcon(),
                        familyName = if(mainPageState.value.isReady())
                            mainPageState.value.data.topBarElements.firstOrNull()?.familyName
                        else null
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
                } else {
                    HomePageTopBar(
                        onTapLeft = {
                            mainPageViewModel.hideShowFamilyNewIcon()
                            onTapLeft()
                        },
                        onTapAlarm = onTapAlarm,
                        onTapRight = onTapRight,
                        isNewIconEnabled = mainPageViewModel.shouldShowFamilyNewIcon(),
                        familyName = if(mainPageNightState.value.isReady())
                            mainPageNightState.value.data.topBarElements.firstOrNull()?.familyName
                        else null
                    )
                    NightHomePageContent(
                        mainPageState = mainPageNightViewModel.uiState,
                        postViewTypeState = postViewTypeState,
                        onTapViewPost = onTapViewPost,
                        onTapProfile = onTapProfile,
                        onTapInvite = onTapInvite,
                        onTapPick = onTapPick,
                        onRefresh = {
                            mainPageNightViewModel.invoke(Arguments())
                        },
                        deferredPickStateSet = mainPageViewModel.deferredPickMembersSet
                    )
                }

            }
            if (postViewType == PostType.SURVIVAL) {
                HomePageSurvivalUploadButton(
                    onTap = onTapUpload,
                    isLoading = !mainPageState.value.isReady(),
                    isUploadAbleTime = remember { gapUntilNext() > 0 },
                    isAlreadyUploaded = !mainPageState.value.isReady() ||
                            mainPageState.value.data.isMeSurvivalUploadedToday,
                    pickers = if (mainPageState.value.isReady()) mainPageState.value.data.pickers
                    else emptyList(),
                    onTapDisabled = {
                        if (gapUntilNext() <= 0) {
                            onTapNight()
                        }
                    }
                )
            } else {
                HomePageMissionUploadButton(
                    onTap = onTapMissionUpload,
                    isLoading = mainPageState.value.isLoading(),
                    isMeUploadedToday = mainPageState.value.isReady()
                            && mainPageState.value.data.isMeSurvivalUploadedToday,
                    isMissionUnlocked = mainPageState.value.isReady() && mainPageState.value.data.isMissionUnlocked,
                    isMeMissionUploaded = mainPageState.value.isReady()
                            && mainPageState.value.data.isMeMissionUploadedToday,
                    onTapDisabled = {
                        if (gapUntilNext() <= 0) {
                            onTapNight()
                        }
                    }
                )
            }

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
                HomePageTopBar(
                    isNewIconEnabled = true,
                    familyName = null,
                )
                HomePageContent(
                    mainPageState = MutableStateFlow(APIResponse.idle()),
                    deferredPickStateSet = MutableStateFlow(emptySet())
                )
            }
            HomePageSurvivalUploadButton()
        }

    }
}

