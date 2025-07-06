package com.no5ing.bbibbi.presentation.feature.view_controller.main

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarPick
import com.no5ing.bbibbi.presentation.feature.view.common.GenericPopup
import com.no5ing.bbibbi.presentation.feature.view.main.home.HomePage
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewPageController.goCameraViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarDetailPageController.goCalendarDetailPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarPageController.goCalendarPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyListPageController.goFamilyListPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.MissionUploadPageController.goMissionUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.NotificationPageController.goNotificationPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostReUploadPageController.goPostReUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostUploadPageController.goPostUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostViewPageController.goPostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.UploadMissionPageController.goMissionCameraPage
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.PickMemberViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.exit
import com.no5ing.bbibbi.util.gapUntilNext
import com.no5ing.bbibbi.util.localResources

object HomePageController : NavigationDestination(
    route = mainHomePageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val snackBarHost = LocalSnackbarHostState.current
        val pickMemberViewModel = hiltViewModel<PickMemberViewModel>()
        val mainPageViewModel = hiltViewModel<MainPageViewModel>()
        var isPickDialogVisible by remember { mutableStateOf(false) }
        var isRequireSurvivalDialogVisible by remember { mutableStateOf(false) }
        var isTryMissionPictureDialogVisible by remember { mutableStateOf(false) }
        var isNightTimeDialogVisible by remember { mutableStateOf(false) }
        var tryPickDialogMember by remember { mutableStateOf<MainPageTopBarModel?>(null) }
        val pickState = pickMemberViewModel.uiState.collectAsState()
        val postViewTypeState = remember { mutableStateOf(PostType.SURVIVAL) }
        val resources = localResources()
        LaunchedEffect(pickState.value.status) {
            if (!pickState.value.isIdle()) {
                mainPageViewModel.invoke(Arguments())
            }
        }
        LaunchedEffect(postViewTypeState.value) {
            if (postViewTypeState.value == PostType.MISSION) {
                //미션 피드 진입
                val mainPageState = mainPageViewModel.uiState.value
                if (mainPageState.isReady()
                    && mainPageState.data.isMissionUnlocked
                    && mainPageState.data.isMeSurvivalUploadedToday
                    && !mainPageState.data.isMeMissionUploadedToday
                ) {
                    if (mainPageViewModel.isMissionPopupShowable())
                        isTryMissionPictureDialogVisible = true
                }
            }
        }

        GenericPopup(
            enabledState = isPickDialogVisible,
            title = stringResource(id = R.string.home_check_survival),
            description = stringResource(
                id = R.string.home_check_survival_description,
                tryPickDialogMember?.displayName ?: ""
            ),
            image = painterResource(id = R.drawable.mission_require_survival),
            confirmText = stringResource(id = R.string.home_check_survival_confirm),
            cancelText = stringResource(id = R.string.home_check_survival_cancel),
            onTapConfirm = {
                isPickDialogVisible = false
                mainPageViewModel.addPickMembersSet(tryPickDialogMember?.memberId ?: "")
                snackBarHost.showSnackBarWithDismiss(
                    message = resources.getString(
                        R.string.home_check_survival_snack,
                        tryPickDialogMember?.displayName ?: ""
                    ),
                    actionLabel = snackBarPick,
                )
                pickMemberViewModel.invoke(
                    Arguments(
                        arguments = mapOf(
                            "memberId" to (tryPickDialogMember?.memberId ?: "")
                        )
                    )
                )
            },
            onTapCancel = {
                isPickDialogVisible = false
            }
        )
        GenericPopup(
            enabledState = isRequireSurvivalDialogVisible,
            title = stringResource(id = R.string.home_survival_first),
            description = stringResource(id = R.string.home_survival_first_description),
            image = painterResource(id = R.drawable.mission_require_survival),
            confirmText = stringResource(id = R.string.home_survival_first_confirm),
            cancelText = stringResource(id = R.string.home_survival_first_cancel),
            onTapConfirm = {
                isRequireSurvivalDialogVisible = false
                navController.goPostUploadPage()
                navController.goCameraViewPage()
            },
            onTapCancel = {
                isRequireSurvivalDialogVisible = false
            }
        )
        GenericPopup(
            enabledState = isNightTimeDialogVisible,
            title = "현재는 사진 업로드가 어려워요",
            description = "내일 12시부터 사용이 가능하니,\n조금만 기다려 주세요.",
            image = painterResource(id = R.drawable.sleep_bibbi),
            confirmText = "홈으로 이동",
            cancelText = "앱 종료하기",
            onTapConfirm = {
                isNightTimeDialogVisible = false
            },
            onTapCancel = {
                isNightTimeDialogVisible = false
                navController.context.exit()
            },
            onTapBackground = {
                isNightTimeDialogVisible = false
            }
        )
        GenericPopup(
            enabledState = isTryMissionPictureDialogVisible,
            title = stringResource(id = R.string.home_mission_key),
            description = stringResource(id = R.string.home_mission_key_description),
            image = painterResource(id = R.drawable.mission_key),
            confirmText = stringResource(id = R.string.home_mission_key_confirm),
            cancelText = stringResource(id = R.string.home_mission_key_cancel),
            onTapConfirm = {
                isTryMissionPictureDialogVisible = false
                navController.goMissionUploadPage()
                navController.goMissionCameraPage()
            },
            onTapCancel = {
                isTryMissionPictureDialogVisible = false
            }
        )
        HomePage(
            onTapLeft = {
                navController.goFamilyListPage()
            },
            onTapRight = {
                navController.goCalendarPage()
            },
            onTapProfile = {
                navController.goProfilePage(it)
            },
            onTapAlarm = {
               navController.goNotificationPage()
            },
            onTapContent = {
               navController.goPostViewPage(it)
            },
            onTapUpload = {
                navController.goPostUploadPage()
                navController.goCameraViewPage()
            },
            onTapInvite = {
                navController.goFamilyListPage()
            },
            onUnsavedPost = {
                navController.goPostReUploadPage(it.toString())
            },
            onTapPick = {
                tryPickDialogMember = it
                isPickDialogVisible = true
            },
            onTapMissionUpload = {
                val uiValue = mainPageViewModel.uiState.value
                if (uiValue.isReady()
                    && uiValue.data.isMissionUnlocked
                    && !uiValue.data.isMeSurvivalUploadedToday
                    && !uiValue.data.isMeMissionUploadedToday
                ) {
                    isRequireSurvivalDialogVisible = true
                } else if (uiValue.isReady()
                    && !uiValue.data.isMeMissionUploadedToday
                    && uiValue.data.isMeSurvivalUploadedToday
                    && uiValue.data.isMissionUnlocked
                    && gapUntilNext() > 0
                ) {
                    //MISSION UPLOAD PAGE
                    navController.goMissionUploadPage()
                    navController.goMissionCameraPage()
                }
            },
            mainPageViewModel = mainPageViewModel,
            postViewTypeState = postViewTypeState,
            onTapViewPost = { date ->
                navController.goCalendarDetailPage(date)
            },
            onTapNight = {
                isNightTimeDialogVisible = true
            }
        )
    }

    fun NavHostController.goHomePage() {
        navigate(route = mainPageRoute)
    }
}
