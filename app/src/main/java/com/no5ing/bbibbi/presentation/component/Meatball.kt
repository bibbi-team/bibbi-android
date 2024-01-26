package com.no5ing.bbibbi.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun MeatBall(
    meatBallSize: Int = 3,
    currentPage: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(meatBallSize) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPage == index) MaterialTheme.bbibbiScheme.backgroundSecondary else MaterialTheme.bbibbiScheme.backgroundSecondary.copy(
                            alpha = 0.2f
                        )
                    )
            )
        }
    }
}


