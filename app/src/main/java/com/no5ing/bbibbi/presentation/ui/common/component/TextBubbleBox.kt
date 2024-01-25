package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.util.toCodePointList
import kotlin.streams.toList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.TextBubbleBox(
    text: String,
    alignment: Alignment = Alignment.Center,
    textStyle: TextStyle  = MaterialTheme.bbibbiTypo.headOne,
    textColor: Color = MaterialTheme.bbibbiScheme.white,
) {
    val wordList = text.toCodePointList()
    var columnAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        columnAppeared = true
    }
    Box(
        modifier = Modifier.align(alignment)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                wordList.size
            ) { index ->
                var itemAppeared by remember { mutableStateOf(!columnAppeared) }
                LaunchedEffect(Unit) {
                    itemAppeared = true
                }
                val character = wordList[index]
                AnimatedVisibility(
                    visible = itemAppeared,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkOut(),
                    modifier = Modifier.animateItemPlacement()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .size(width = 41.dp, height = 65.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character,
                            color = textColor,
                            style = textStyle,
                        )
                    }
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