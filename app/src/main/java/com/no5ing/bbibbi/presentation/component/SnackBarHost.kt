package com.no5ing.bbibbi.presentation.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CustomSnackBarHost(
    hostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) { snackbarData ->
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(44.dp))
                .background(MaterialTheme.bbibbiScheme.backgroundSecondary)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (snackbarData.visuals.actionLabel ?: "") {
                    snackBarWarning -> WarningIcon()
                    snackBarInfo -> InfoIcon()
                    snackBarCamera -> CameraIcon()
                    snackBarFire -> FireIcon()
                    else -> Icon(
                        imageVector = Icons.Default.Email,
                        tint = MaterialTheme.bbibbiScheme.white,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = snackbarData.visuals.message,
                    color = MaterialTheme.bbibbiScheme.white
                )
            }
        }
    }
}

const val snackBarWarning = "warning"
const val snackBarInfo = "info"
const val snackBarCamera = "camera"
const val snackBarSuccess = "info"
const val snackBarFire = "fire"

@Composable
private fun WarningIcon() {
    Icon(
        painter = painterResource(id = R.drawable.warning_circle_icon),
        tint = MaterialTheme.bbibbiScheme.warningRed,
        contentDescription = null
    )
}

@Composable
private fun FireIcon() {
    Icon(
        painter = painterResource(id = R.drawable.fire_icon),
        tint = MaterialTheme.bbibbiScheme.warningRed,
        contentDescription = null
    )
}

@Composable
private fun CameraIcon() {
    Icon(
        painter = painterResource(id = R.drawable.camera_icon),
        tint = MaterialTheme.bbibbiScheme.textSecondary,
        contentDescription = null
    )
}

@Composable
private fun InfoIcon() {
    Icon(
        imageVector = Icons.Default.Info,
        tint = MaterialTheme.bbibbiScheme.icon,
        contentDescription = null
    )
}

fun SnackbarHostState.dismissIfShown() {
    if (currentSnackbarData != null) {
        currentSnackbarData?.dismiss()
    }
}

fun SnackbarHostState.showSnackBarWithDismiss(
    message: String,
    actionLabel: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    CoroutineScope(Dispatchers.Main).launch {
        dismissIfShown()
        showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
    }
}