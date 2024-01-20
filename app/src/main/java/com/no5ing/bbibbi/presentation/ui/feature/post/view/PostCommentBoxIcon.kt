package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo

@Composable
fun PostCommentBoxIcon(
    commentCount: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
            .padding(vertical = 5.dp, horizontal = 7.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.message_icon),
                contentDescription = null,
                tint = MaterialTheme.bbibbiScheme.textPrimary,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = stringResource(id = R.string.comment_count, commentCount),
                color = MaterialTheme.bbibbiScheme.textPrimary,
                style = MaterialTheme.bbibbiTypo.bodyOneBold,
            )
        }

    }
}