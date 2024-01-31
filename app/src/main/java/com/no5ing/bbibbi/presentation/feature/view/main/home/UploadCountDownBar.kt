package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.gapUntilNext
import kotlinx.coroutines.delay

@Composable
fun UploadCountDownBar(
    timeStr: MutableState<String> = remember {
        mutableStateOf("00:00:00")
    },
    warningState: MutableState<Int> = remember {
        mutableIntStateOf(0)
    },
) {
    LaunchedEffect(Unit) {
        while (true) {
            val gap = gapUntilNext()
            val hourLeft = gap / 3600
            val minuteLeft = gap / 60 % 60
            val secondLeft = gap % 60

            val gapStr = if (gap < 0) {
                "00:00:00"
            } else "${String.format("%02d", hourLeft)}:${
                String.format(
                    "%02d",
                    minuteLeft
                )
            }:${String.format("%02d", secondLeft)}"
            timeStr.value = gapStr
            warningState.value = if (gap < 0) 2 else if (hourLeft < 1) 1 else 0
            delay(1000L)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeStr.value,
            style = MaterialTheme.bbibbiTypo.headOne,
            color = if (warningState.value == 1) MaterialTheme.bbibbiScheme.warningRed else MaterialTheme.bbibbiScheme.white,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (warningState.value == 1) stringResource(id = R.string.home_time_not_much)
                else stringResource(id = R.string.home_image_on_duration),
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )

            if (warningState.value == 1) {
                Image(
                    painter = painterResource(id = R.drawable.fire_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.smile_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}