package com.no5ing.bbibbi.presentation.ui.common.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme

@Composable
fun ToggleButton(
    isToggled: Boolean,
    onTap: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable {
                onTap()
            }
            .width(45.dp)
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(MaterialTheme.bbibbiScheme.gray600, RoundedCornerShape(8.dp))
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier.offset {
                IntOffset(x = -15, y = 0)
            }
        ) {
            AnimatedVisibility(
                visible = isToggled,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.check_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 45.dp, height = 31.dp),
                )
            }
        }

    }
}