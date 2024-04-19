package com.no5ing.bbibbi.presentation.feature.view.main.home

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
fun TryPickPopup(
    enabledState: Boolean = false,
    targetNickname: String = "",
    onTapNow: () -> Unit = {},
    onTapLater: () -> Unit = {},
) {
    if (enabledState) {
        BasicAlertDialog(
            onDismissRequest = onTapLater,
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
                            onClick = onTapNow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.mainYellow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                "지금 하기",
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = Color(0xff242427)
                            )
                        }
                        Button(
                            onClick = onTapLater,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.button
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                "다음에 하기",
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = MaterialTheme.bbibbiScheme.icon
                            )
                        }
                    }
                },
                icon = null,
                title = {
                    Text(
                        "생존 확인하기",
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
                            "${targetNickname}님의 생존 여부를 물어볼까요?\n지금 알림이 전송됩니다.",
                            color = MaterialTheme.bbibbiScheme.textSecondary,
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Image(
                            painter = painterResource(id = R.drawable.lying_bibbi),
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