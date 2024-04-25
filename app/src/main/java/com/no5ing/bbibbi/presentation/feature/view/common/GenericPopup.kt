package com.no5ing.bbibbi.presentation.feature.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.feature.view.common.AlertDialogContent
import com.no5ing.bbibbi.presentation.feature.view.common.AlertDialogFlowRow
import com.no5ing.bbibbi.presentation.feature.view.common.ButtonsCrossAxisSpacing
import com.no5ing.bbibbi.presentation.feature.view.common.ButtonsMainAxisSpacing
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericPopup(
    enabledState: Boolean = false,
    title: String,
    description: String,
    image: Painter,
    confirmText: String,
    cancelText: String,
    onTapConfirm: () -> Unit = {},
    onTapCancel: () -> Unit = {},
) {
    if (enabledState) {
        BasicAlertDialog(
            onDismissRequest = onTapCancel,
            modifier = Modifier,
            properties = DialogProperties()
        ) {
            AlertDialogContent(
                textPadding = PaddingValues(0.dp),
                buttons = {
                    AlertDialogFlowRow(
                        mainAxisSpacing = ButtonsMainAxisSpacing,
                        crossAxisSpacing = 8.dp
                    ) {
                        Button(
                            onClick = onTapConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.mainYellow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                confirmText,
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = Color(0xff242427)
                            )
                        }
                        Button(
                            onClick = onTapCancel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.button
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                cancelText,
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = MaterialTheme.bbibbiScheme.icon
                            )
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
                        Spacer(modifier = Modifier.height(24.dp))
                        Image(
                            painter = image,
                            contentDescription = null,
                        )
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
    }
}