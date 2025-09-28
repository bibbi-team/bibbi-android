package com.no5ing.bbibbi.presentation.feature.view.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.dpToPx
import timber.log.Timber


@Composable
fun PostTypeSwitchButton(
    isLocked: Boolean = true,
    state: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) }
) {
    //val isSurvival = state.value == PostType.SURVIVAL
    val widthMax = 230.dp.dpToPx()
    val buttonPosition: Dp by animateDpAsState(
        targetValue =
        when(state.value) {
            PostType.SURVIVAL -> 0.dp
            PostType.MISSION -> 69.dp
            PostType.AI_IMAGE -> 140.dp
        }, animationSpec = tween(
            durationMillis = 130,
            easing = LinearEasing,
        ),
        label = ""
    )
    val survivalButtonColor: Color by animateColorAsState(
        targetValue = if (state.value == PostType.SURVIVAL) MaterialTheme.bbibbiScheme.backgroundPrimary else MaterialTheme.bbibbiScheme.gray500,
        label = "",
    )
    val missionButtonColor: Color by animateColorAsState(
        targetValue = if (state.value == PostType.MISSION) MaterialTheme.bbibbiScheme.backgroundPrimary else MaterialTheme.bbibbiScheme.gray500,
        label = "",
    )
    Box{
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .size(width = 230.dp, height = 40.dp)
                .background(MaterialTheme.bbibbiScheme.backgroundHover, RoundedCornerShape(40.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val divVal = widthMax / 3
                        if (offset.x < divVal) {
                            state.value = PostType.SURVIVAL
                        } else if (offset.x < divVal * 2) {
                            state.value = PostType.MISSION
                        } else {
                            state.value = PostType.AI_IMAGE
                        }
                        Timber.d("offset: $offset")
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = buttonPosition)
                    .size(width = 70.dp, height = 40.dp)
                    .background(MaterialTheme.bbibbiScheme.iconSelected, RoundedCornerShape(40.dp))
            )
            Box(
                modifier = Modifier.width(138.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .width(138.dp)
                        .padding(
                            start = 24.dp,
                            end = if (isLocked) 14.dp else 24.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(id = R.string.post_type_survival),
                        style = MaterialTheme.bbibbiTypo.bodyTwoBold,
                        color = survivalButtonColor
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.post_type_mission),
                            style = MaterialTheme.bbibbiTypo.bodyTwoBold,
                            color = missionButtonColor,
                        )
                        if (isLocked) {
                            Image(
                                modifier = Modifier.size(12.dp),
                                painter = painterResource(id = R.drawable.lock_icon),
                                contentDescription = "locked"
                            )
                        }
                    }
                }
            }

        }
        Box(
            modifier = Modifier.offset(x = 136.dp)
        ) {
            Image(
                modifier = Modifier
                    .background(Color.Transparent)
                    .size(width = 94.dp, height = 40.dp),
                painter = painterResource(id = R.drawable.photo_studio),
                contentDescription = "photo studio",
            )
        }

    }


}

@Preview
@Composable
fun FeedTypeSwitchButtonPreview() {
    BBiBBiPreviewSurface {
        PostTypeSwitchButton()
    }

}