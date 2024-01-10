package com.no5ing.bbibbi.presentation.ui.feature.landing.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.landing.onboarding.OnBoardingPageState
import com.no5ing.bbibbi.presentation.state.landing.onboarding.rememberOnBoardingPageState
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.MeatBall
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.viewmodel.family.FamilyRegistrationViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.emptyPermissionState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun OnBoardingPage(
    onDispose: () -> Unit = {},
    onBoardingPageState: OnBoardingPageState = rememberOnBoardingPageState(),
    familyRegistrationViewModel: FamilyRegistrationViewModel = hiltViewModel(),
) {
    val registrationState = familyRegistrationViewModel.uiState.collectAsState()
    val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) { isAccepted ->
            if (!isAccepted) {
                //먼가 메세지 띄우나?
                Timber.d("[OnBoarding] Noti Perm Not Accepted!!")
            }
            familyRegistrationViewModel.invoke(Arguments())
        }
    else
        remember { emptyPermissionState }
    val snackBarHost = LocalSnackbarHostState.current
    val resources = localResources()
    LaunchedEffect(registrationState.value) {
        when (registrationState.value.status) {
            is APIResponse.Status.SUCCESS -> {
                onDispose()
            }

            is APIResponse.Status.ERROR -> {
                val errMessage = resources.getErrorMessage(registrationState.value.errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errMessage,
                    actionLabel = snackBarWarning,
                )
                familyRegistrationViewModel.resetState()
            }

            else -> {}
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HorizontalPager(state = onBoardingPageState.pagerState) {
                when (it) {
                    0 -> OnBoardingFirstPage()
                    1 -> OnBoardingSecondPage()
                    2 -> OnBoardingThirdPage()
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                )
            ) {
                MeatBall(
                    meatBallSize = onBoardingPageState.pagerState.pageCount,
                    currentPage = onBoardingPageState.pagerState.currentPage
                )
                CTAButton(
                    text = if (familyRegistrationViewModel.hasRegistrationToken)
                        stringResource(id = R.string.onboarding_next_join)
                    else
                        stringResource(id = R.string.onboarding_next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                    ,
                    contentPadding = PaddingValues(vertical = 18.dp),
                    onClick = {
                        if (!perm.status.isGranted) {
                            perm.launchPermissionRequest()
                        } else {
                            //이미 동의했거나 API버전 낮음
                            familyRegistrationViewModel.invoke(Arguments())
                        }
                    },
                    isActive = onBoardingPageState.pagerState.currentPage == 2
                            && registrationState.value.isIdle(),
                )
            }
        }
    }
}

