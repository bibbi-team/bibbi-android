package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
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
import com.no5ing.bbibbi.presentation.feature.view.main.home.TryPickPopup
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewPageController.goCameraViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarDetailPageController.goCalendarDetailPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarPageController.goCalendarPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyListPageController.goFamilyListPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.MissionUploadPageController.goMissionUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostReUploadPageController.goPostReUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostUploadPageController.goPostUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostViewPageController.goPostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.UploadMissionPageController.goMissionCameraPage
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.PickMemberViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.gapUntilNext

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
        var tryPickDialogMember by remember { mutableStateOf<MainPageTopBarModel?>(null) }
        val pickState = pickMemberViewModel.uiState.collectAsState()
        val postViewTypeState = remember { mutableStateOf(PostType.SURVIVAL) }
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
        TryPickPopup(
            enabledState = isPickDialogVisible,
            targetNickname = tryPickDialogMember?.displayName ?: "",
            onTapNow = {
                isPickDialogVisible = false
                mainPageViewModel.addPickMembersSet(tryPickDialogMember?.memberId ?: "")
                snackBarHost.showSnackBarWithDismiss(
                    message = "${tryPickDialogMember?.displayName ?: ""}님에게 생존신고 알림을 보냈어요",
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
            onTapLater = {
                isPickDialogVisible = false
            }
        )
        GenericPopup(
            enabledState = isRequireSurvivalDialogVisible,
            title = "생존신고 사진을 먼저 찍으세요!",
            description = "미션 사진을 올리려면\n생존신고 사진을 먼저 업로드해야해요.",
            image = painterResource(id = R.drawable.mission_require_survival),
            confirmText = "생존신고 먼저 하기",
            cancelText = "다음에 하기",
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
            enabledState = isTryMissionPictureDialogVisible,
            title = "미션 열쇠 획득!",
            description = "열쇠를 획득해 잠금이 해제되었어요.\n미션 사진을 찍을 수 있어요!",
            image = painterResource(id = R.drawable.mission_key),
            confirmText = "미션 사진 찍기",
            cancelText = "닫기",
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
            }
        )
    }

    fun NavHostController.goHomePage() {
        navigate(route = mainPageRoute)
    }
}
