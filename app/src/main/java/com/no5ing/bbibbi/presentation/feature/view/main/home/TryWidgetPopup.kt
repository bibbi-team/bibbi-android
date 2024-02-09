package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun TryWidgetPopup() {
    val enabledState = remember { mutableStateOf(true) }
    if (enabledState.value) {
        AlertDialog(
            onDismissRequest = {
                enabledState.value = false
            },
            modifier = Modifier,
            properties = DialogProperties()
        ) {
            AlertDialogContent(
                textPadding = PaddingValues(0.dp),
                buttons = {
                    AlertDialogFlowRow(
                        mainAxisSpacing = ButtonsMainAxisSpacing,
                        crossAxisSpacing = ButtonsCrossAxisSpacing
                    ) {
                        Button(
                            onClick = {
                                enabledState.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.mainYellow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                stringResource(id = R.string.dialog_confirm),
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = Color(0xff242427)
                            )
                        }
                    }
                },
                icon = null,
                title = {
                    Text(
                        stringResource(id = R.string.try_widget_title),
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
                            stringResource(id = R.string.try_widget_description),
                            color = MaterialTheme.bbibbiScheme.textSecondary,
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.try_widget_image),
                            contentDescription = null,
                        )
                    }

                },
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                iconContentColor = AlertDialogDefaults.iconContentColor,
                titleContentColor = AlertDialogDefaults.titleContentColor,
                textContentColor = AlertDialogDefaults.textContentColor,
                modifier = Modifier.padding(0.dp)
            )

        }
    }
}