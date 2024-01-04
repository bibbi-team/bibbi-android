package com.no5ing.bbibbi.presentation.ui.feature.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R

@Composable
fun CustomAlertDialog(
    enabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    title: String,
    description: String,
    confirmRequest: () -> Unit = {},
    dismissRequest: () -> Unit = { enabledState.value = false },
    cancelRequest: () -> Unit = { enabledState.value = false },
    confirmMessage: String = stringResource(id = R.string.dialog_confirm),
    cancelMessage: String = stringResource(id = R.string.dialog_cancel),
) {
    if (enabledState.value) {
        AlertDialog(
            onDismissRequest = cancelRequest,
            title = {
                Text(
                    title,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    description,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            dismissButton = {
                Button(
                    onClick = dismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        cancelMessage
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = confirmRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Text(confirmMessage)
                }
            },
            shape = RoundedCornerShape(14.dp),
            containerColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun AlbumCameraSelectDialog(
    enabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onAlbum: () -> Unit = {},
    onCamera: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_image_upload_title),
        description = stringResource(id = R.string.dialog_image_upload_message),
        confirmMessage = stringResource(id = R.string.dialog_image_upload_camera),
        cancelMessage = stringResource(id = R.string.dialog_image_upload_album),
        enabledState = enabledState,
        confirmRequest = onCamera,
        dismissRequest = onAlbum,
        cancelRequest = onCancel,
    )
}