package com.no5ing.bbibbi.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.no5ing.bbibbi.R
import java.util.Locale

@Composable
fun getEmojiResource(emojiName: String): Painter = when (emojiName.lowercase(Locale.ROOT)) {
    "emoji_1" -> painterResource(id = R.drawable.emoji_1)
    "emoji_2" -> painterResource(id = R.drawable.emoji_2)
    "emoji_3" -> painterResource(id = R.drawable.emoji_3)
    "emoji_4" -> painterResource(id = R.drawable.emoji_4)
    "emoji_5" -> painterResource(id = R.drawable.emoji_5)
    else -> painterResource(id = R.drawable.emoji_1) //TBD
}

@Composable
fun getDisabledEmojiResource(emojiName: String): Painter = when (emojiName.lowercase(Locale.ROOT)) {
    "emoji_1" -> painterResource(id = R.drawable.emoji_disabled_1)
    "emoji_2" -> painterResource(id = R.drawable.emoji_disabled_2)
    "emoji_3" -> painterResource(id = R.drawable.emoji_disabled_3)
    "emoji_4" -> painterResource(id = R.drawable.emoji_disabled_4)
    "emoji_5" -> painterResource(id = R.drawable.emoji_disabled_5)
    else -> painterResource(id = R.drawable.emoji_disabled_1) //TBD
}

@Composable
fun getRealEmojiResource(emojiName: String): Painter = when (emojiName.lowercase(Locale.ROOT)) {
    "emoji_1" -> painterResource(id = R.drawable.real_emoji_1)
    "emoji_2" -> painterResource(id = R.drawable.real_emoji_2)
    "emoji_3" -> painterResource(id = R.drawable.real_emoji_3)
    "emoji_4" -> painterResource(id = R.drawable.real_emoji_4)
    "emoji_5" -> painterResource(id = R.drawable.real_emoji_5)
    else -> painterResource(id = R.drawable.real_emoji_2) //TBD
}

val emojiList = listOf(
    "emoji_1",
    "emoji_2",
    "emoji_3",
    "emoji_4",
    "emoji_5",
)