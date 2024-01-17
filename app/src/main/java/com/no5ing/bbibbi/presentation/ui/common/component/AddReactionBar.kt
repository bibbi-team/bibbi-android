package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getEmojiResource

@Composable
fun AddReactionBar(
    onTapEmoji: (String) -> Unit,
    onDispose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        OuterClickListener(onClick = onDispose)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(1000.dp))
                .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                emojiList.forEach { emojiType ->
                    Image(
                        painter = getEmojiResource(emojiName = emojiType),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                onTapEmoji(emojiType)
                            }
                    )
                }
            }

        }
    }
}
