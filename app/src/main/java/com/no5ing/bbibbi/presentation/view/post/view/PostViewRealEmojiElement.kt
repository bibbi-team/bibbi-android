package com.no5ing.bbibbi.presentation.view.post.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.getRealEmojiResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostViewRealEmojiElement(
    iconUrl: String,
    emojiCnt: Int,
    emojiType: String,
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
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
            ) {
                Box {
                    AsyncImage(
                        model = asyncImagePainter(iconUrl),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape),
                    )
                }
                Box(
                    modifier = Modifier.offset(x = 2.5.dp, y = 2.5.dp)
                ) {
                    Image(
                        painter = getRealEmojiResource(emojiName = emojiType),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(13.dp),
                    )
                }
            }
            Text(
                text = emojiCnt.toString(),
                color = if (isMeReacted) MaterialTheme.bbibbiScheme.mainYellow
                else MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
        }

    }
}