package com.no5ing.bbibbi.presentation.feature.view.main.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.notification.NotificationModel
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.feature.view_model.notification.GetCurrentDisplayableNotificationViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun NotificationPage(
    onDispose: () -> Unit,
    onTapNotification: (NotificationModel) -> Unit,
    getCurrentDisplayableNotificationViewModel: GetCurrentDisplayableNotificationViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        getCurrentDisplayableNotificationViewModel.invoke(Arguments())
    }

    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            NotificationPageTopBar(
                onDispose = onDispose
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.bbibbiScheme.backgroundSecondary
            )
            NotificationPageContent(
                notificationState = getCurrentDisplayableNotificationViewModel.uiState.collectAsState(),
                onRefresh = {
                    getCurrentDisplayableNotificationViewModel.invoke(Arguments())
                },
                onTapNotification = onTapNotification,
            )
        }
    }
}


@Preview(
    showBackground = true,
    name = "NotificationPagePreview",
    showSystemUi = true
)
@Composable
fun SettingHomePagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            NotificationPageTopBar()
            Spacer(modifier = Modifier.height(24.dp))
            NotificationPageContent(
                notificationState = remember {
                    mutableStateOf(
                        APIResponse.success(
                            listOf(
                                NotificationModel.mock(),
                                NotificationModel.mock(),
                                NotificationModel.mock(),
                            )
                        )
                    )
                }
            )
        }
    }
}

