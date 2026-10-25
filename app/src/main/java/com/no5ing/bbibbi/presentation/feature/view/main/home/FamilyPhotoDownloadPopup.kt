package com.no5ing.bbibbi.presentation.feature.view.main.home

import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.feature.view.common.AlertDialogContent
import com.no5ing.bbibbi.presentation.feature.view.common.AlertDialogFlowRow
import com.no5ing.bbibbi.presentation.feature.view.common.ButtonsCrossAxisSpacing
import com.no5ing.bbibbi.presentation.feature.view.common.ButtonsMainAxisSpacing
import com.no5ing.bbibbi.presentation.feature.view_model.FamilyPhotoDownloadState
import com.no5ing.bbibbi.presentation.feature.view_model.FamilyPhotoDownloadViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

/**
 * 서비스 종료 안내 + 가족 사진 전체 다운로드 팝업.
 * 앱을 켤 때마다(프로세스당 한 번) 홈 화면에서 노출된다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyPhotoDownloadPopup(
    viewModel: FamilyPhotoDownloadViewModel = hiltViewModel(),
) {
    var visible by remember { mutableStateOf(viewModel.shouldShowPopup()) }
    LaunchedEffect(Unit) { viewModel.markPopupShown() }

    val state by viewModel.state.collectAsState()
    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) viewModel.download(uri)
    }
    val startDownload = { saveLauncher.launch(viewModel.defaultFileName()) }
    val close = {
        viewModel.resetState()
        visible = false
    }

    if (!visible) return

    val isDownloading = state is FamilyPhotoDownloadState.Downloading
    BasicAlertDialog(
        onDismissRequest = { if (!isDownloading) close() },
        properties = DialogProperties(
            dismissOnBackPress = !isDownloading,
            dismissOnClickOutside = !isDownloading,
        ),
    ) {
        when (val current = state) {
            FamilyPhotoDownloadState.Idle -> PopupContent(
                title = stringResource(R.string.service_end_popup_title),
                description = stringResource(R.string.service_end_popup_description),
                primaryText = stringResource(R.string.service_end_popup_download),
                onPrimary = startDownload,
                secondaryText = stringResource(R.string.service_end_popup_later),
                onSecondary = close,
            )

            is FamilyPhotoDownloadState.Downloading -> PopupContent(
                title = stringResource(R.string.service_end_downloading_title),
                description = stringResource(R.string.service_end_downloading_description),
                secondaryText = stringResource(R.string.service_end_download_cancel),
                onSecondary = viewModel::cancel,
            ) {
                DownloadProgress(current)
            }

            FamilyPhotoDownloadState.Success -> PopupContent(
                title = stringResource(R.string.service_end_download_success_title),
                description = stringResource(R.string.service_end_download_success_description),
                primaryText = stringResource(R.string.service_end_download_close),
                onPrimary = close,
            )

            FamilyPhotoDownloadState.Failed -> PopupContent(
                title = stringResource(R.string.service_end_download_failed_title),
                description = stringResource(R.string.service_end_download_failed_description),
                primaryText = stringResource(R.string.service_end_download_retry),
                onPrimary = startDownload,
                secondaryText = stringResource(R.string.service_end_download_close),
                onSecondary = close,
            )
        }
    }
}

@Composable
private fun DownloadProgress(state: FamilyPhotoDownloadState.Downloading) {
    val context = LocalContext.current
    val total = state.totalBytes
    Spacer(modifier = Modifier.height(20.dp))
    if (total != null) {
        LinearProgressIndicator(
            progress = { (state.downloadedBytes.toFloat() / total).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.bbibbiScheme.mainYellow,
            trackColor = MaterialTheme.bbibbiScheme.button,
        )
    } else {
        // 서버가 스트리밍으로 내려줘서 전체 크기를 모를 수 있다.
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.bbibbiScheme.mainYellow,
            trackColor = MaterialTheme.bbibbiScheme.button,
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = if (state.downloadedBytes == 0L) {
            stringResource(R.string.service_end_downloading_preparing)
        } else {
            stringResource(
                R.string.service_end_downloading_progress,
                Formatter.formatShortFileSize(context, state.downloadedBytes),
            )
        },
        color = MaterialTheme.bbibbiScheme.textSecondary,
        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PopupContent(
    title: String,
    description: String,
    primaryText: String? = null,
    onPrimary: () -> Unit = {},
    secondaryText: String? = null,
    onSecondary: () -> Unit = {},
    extraContent: @Composable () -> Unit = {},
) {
    AlertDialogContent(
        textPadding = PaddingValues(0.dp),
        buttons = {
            AlertDialogFlowRow(
                mainAxisSpacing = ButtonsMainAxisSpacing,
                crossAxisSpacing = ButtonsCrossAxisSpacing,
            ) {
                if (primaryText != null) {
                    Button(
                        onClick = onPrimary,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.bbibbiScheme.mainYellow
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            primaryText,
                            style = MaterialTheme.bbibbiTypo.bodyOneBold,
                            color = Color(0xff242427)
                        )
                    }
                }
                if (secondaryText != null) {
                    Button(
                        onClick = onSecondary,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.bbibbiScheme.button
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            secondaryText,
                            style = MaterialTheme.bbibbiTypo.bodyOneBold,
                            color = MaterialTheme.bbibbiScheme.icon
                        )
                    }
                }
            }
        },
        icon = null,
        title = {
            Text(
                title,
                color = MaterialTheme.bbibbiScheme.iconSelected,
                style = MaterialTheme.bbibbiTypo.headTwoBold,
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    description,
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                    textAlign = TextAlign.Center,
                )
                extraContent()
                // 본문과 버튼 사이 여백
                Spacer(modifier = Modifier.height(24.dp))
            }
        },
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.bbibbiScheme.backgroundPrimary,
        tonalElevation = AlertDialogDefaults.TonalElevation,
        iconContentColor = AlertDialogDefaults.iconContentColor,
        titleContentColor = AlertDialogDefaults.titleContentColor,
        textContentColor = AlertDialogDefaults.textContentColor,
        modifier = Modifier.padding(0.dp)
    )
}
