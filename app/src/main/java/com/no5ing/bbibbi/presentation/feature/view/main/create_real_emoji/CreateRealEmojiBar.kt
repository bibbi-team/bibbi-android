package com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getDisabledEmojiResource
import com.no5ing.bbibbi.util.getEmojiResource
import com.no5ing.bbibbi.util.getRealEmojiResource

@Composable
fun CreateRealEmojiBar(
    selectedEmoji: String,
    emojiMap: Map<String, MemberRealEmoji>,
    onTapEmoji: (String) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = Modifier
            .background(
                color = MaterialTheme.bbibbiScheme.button,
                shape = RoundedCornerShape(1000.dp)
            )
            .padding(vertical = 10.dp, horizontal = 24.dp)
    ) {
        emojiList.forEach { emojiType ->
            if (emojiMap.containsKey(emojiType)) {
                val realEmoji = emojiMap[emojiType]!!
                Box(
                    modifier = Modifier.clickable {
                        onTapEmoji(emojiType)
                    },
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Box {
                        AsyncImage(
                            model = asyncImagePainter(source = realEmoji.imageUrl),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                        )
                    }
                    Box(
                        modifier = Modifier.offset(x = 4.dp, y = 4.dp)
                    ) {
                        Image(
                            painter = getRealEmojiResource(emojiName = emojiType),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(20.dp),
                        )
                    }
                }
            } else {
                Image(
                    painter = if (selectedEmoji == emojiType) getEmojiResource(emojiName = emojiType)
                    else getDisabledEmojiResource(emojiName = emojiType),
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