package com.no5ing.bbibbi.presentation.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
            .width(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.bbibbiScheme.backgroundHover, RoundedCornerShape(4.dp))
                .align(Alignment.Center)
        )
        Box {
            AnimatedVisibility(
                visible = isToggled,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.check_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 14.5.dp, height = 10.dp),
                )
            }
        }

    }
}