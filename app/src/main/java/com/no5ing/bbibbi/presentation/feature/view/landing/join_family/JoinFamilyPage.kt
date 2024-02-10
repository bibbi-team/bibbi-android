package com.no5ing.bbibbi.presentation.feature.view.landing.join_family

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.CreateFamilyAndJoinViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.dashedBorder
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources

@Composable
fun JoinFamilyPage(
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    onTapJoinWithLink: () -> Unit,
    onFamilyCreationComplete: () -> Unit,
    createFamilyAndJoinViewModel: CreateFamilyAndJoinViewModel = hiltViewModel(),
) {
    val mixPanel = LocalMixpanelProvider.current
    val createNewDialogVisible = remember {
        mutableStateOf(false)
    }
    val meState = retrieveMeViewModel.uiState.collectAsState()
    LaunchedEffect(meState) {
        if (meState.value.isIdle()) {
            retrieveMeViewModel.invoke(Arguments())
        }
    }

    val uiState by createFamilyAndJoinViewModel.uiState.collectAsState()
    val snackBarHost = LocalSnackbarHostState.current
    val resources = localResources()
    LaunchedEffect(uiState) {
        when {
            uiState.isReady() -> onFamilyCreationComplete()
            uiState.isFailed() -> {
                val errMessage = resources.getErrorMessage(uiState.errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errMessage,
                    actionLabel = snackBarWarning,
                )
                createFamilyAndJoinViewModel.resetState()
            }
        }
    }

    CustomAlertDialog(
        enabledState = createNewDialogVisible,
        title = stringResource(id = R.string.create_new_room_dialog_title),
        description = stringResource(id = R.string.create_new_room_dialog_description),
        confirmRequest = {
            mixPanel.track("Click_Create_NewGroup_2")
            createFamilyAndJoinViewModel.invoke(Arguments())
        }
    )
    val nickName = if (meState.value.isReady()) meState.value.data.name else ""
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(id = R.string.join_family_title, nickName),
                style = MaterialTheme.bbibbiTypo.headOne,
                color = MaterialTheme.bbibbiScheme.iconSelected,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                JoinFamilyPageLinkBar(
                    onTap = onTapJoinWithLink,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.bbibbiScheme.backgroundSecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .dashedBorder(
                        width = 1.dp,
                        color = MaterialTheme.bbibbiScheme.gray600,
                        radius = 16.dp
                    )
                    .clickable {
                        mixPanel.track("Click_Create_NewGroup_1")
                        createNewDialogVisible.value = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Box(
                        Modifier.size(50.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus_icon),
                            contentDescription = null,
                            tint = MaterialTheme.bbibbiScheme.textSecondary,
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(13.dp))
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(vertical = 9.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.join_family_without_group),
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                            color = MaterialTheme.bbibbiScheme.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stringResource(id = R.string.join_family_create_group),
                            style = MaterialTheme.bbibbiTypo.headTwoBold,
                            color = MaterialTheme.bbibbiScheme.textPrimary,
                        )
                    }
                }
            }


        }
    }
}