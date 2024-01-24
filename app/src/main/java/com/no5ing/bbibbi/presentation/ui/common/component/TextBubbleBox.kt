package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import kotlin.streams.toList

@Composable
fun BoxScope.TextBubbleBox(
    text: String,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = Modifier.align(alignment)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val wordList = text.codePoints().toList().map { String(Character.toChars(it)) }
            wordList.forEach { character ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .size(width = 41.dp, height = 65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.toString(),
                        color = MaterialTheme.bbibbiScheme.white,
                        style = MaterialTheme.bbibbiTypo.headOne,
                    )
                }
            }

        }
    }
}

@Composable
fun BoxScope.MiniTextBubbleBox(
    text: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = modifier.align(alignment)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val wordList = text.codePoints().toList().map { String(Character.toChars(it)) }
            wordList.forEach { character ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .size(width = 28.dp, height = 41.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.toString(),
                        color = MaterialTheme.bbibbiScheme.white,
                        style = MaterialTheme.bbibbiTypo.headTwoBold,
                    )
                }
            }

        }
    }
}