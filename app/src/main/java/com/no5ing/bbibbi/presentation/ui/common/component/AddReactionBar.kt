package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getDisabledEmojiResource
import com.no5ing.bbibbi.util.getEmojiResource
import com.no5ing.bbibbi.util.getRealEmojiResource

@Composable
fun AddReactionBar(
    onTapEmoji: (String) -> Unit,
    onTapRealEmoji: (MemberRealEmoji) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    onDispose: () -> Unit,
    realEmojiMap: Map<String, MemberRealEmoji>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        OuterClickListener(onClick = onDispose)
        Box(
            modifier = modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(48.dp))
                .background(color = MaterialTheme.bbibbiScheme.button)
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    emojiList.forEach { emojiType ->
                        Image(
                            painter = getEmojiResource(emojiName = emojiType),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onTapEmoji(emojiType)
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    emojiList.forEach { emojiType ->
                        if (realEmojiMap.containsKey(emojiType)) {
                            val realEmoji = realEmojiMap[emojiType]!!
                            Box(
                                modifier = Modifier.clickable {
                                    onTapRealEmoji(realEmoji)
                                },
                                contentAlignment = Alignment.BottomEnd,
                            ) {
                                Box {
                                    AsyncImage(
                                        model = asyncImagePainter(source = realEmoji.imageUrl),
                                        contentDescription = null, // 필수 param
                                        modifier = Modifier
                                            .size(40.dp)
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
                                painter = getDisabledEmojiResource(emojiName = emojiType),
                                contentDescription = null, // 필수 param
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        onTapRealEmojiCreate(emojiType)
                                    },
                                alpha = 0.4f,
                            )
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.camera_button),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onTapRealEmojiCreate(emojiList.first())
                            },
                    )
                }
            }


        }
    }
}
