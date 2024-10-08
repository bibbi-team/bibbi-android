package com.no5ing.bbibbi.presentation.feature.view.main.post_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.getEmojiResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostViewReactionElement(
    emojiType: String,
    emojiCnt: Int,
    isMeReacted: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .let {
                if (isMeReacted)
                    it.border(
                        width = 1.dp,
                        color = MaterialTheme.bbibbiScheme.mainYellow,
                        RoundedCornerShape(100.dp)
                    )
                else
                    it
            }
            .background(
                color = if (isMeReacted) MaterialTheme.bbibbiScheme.mainYellow.copy(alpha = 0.1f) else MaterialTheme.bbibbiScheme.button,
                RoundedCornerShape(100.dp)
            )
            .size(width = 53.dp, height = 36.dp)
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongTap,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = getEmojiResource(emojiName = emojiType),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .size(26.dp)
            )
            Text(
                text = emojiCnt.toString(),
                color = if (isMeReacted) MaterialTheme.bbibbiScheme.mainYellow
                else MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
        }

    }
}