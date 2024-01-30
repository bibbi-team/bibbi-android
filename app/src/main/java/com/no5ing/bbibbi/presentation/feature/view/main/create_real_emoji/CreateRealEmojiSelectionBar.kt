package com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.getEmojiResource

@Composable
fun CreateRealEmojiSelectionBar(
    selectedEmoji: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.bbibbiScheme.gray600, CircleShape)
            )
            Image(
                painter = getEmojiResource(emojiName = selectedEmoji),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                contentScale = ContentScale.FillBounds,
            )
        }

        Text(
            text = stringResource(id = R.string.real_emoji_follow_emoji),
            color = MaterialTheme.bbibbiScheme.emojiYellow,
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
        )

    }
}