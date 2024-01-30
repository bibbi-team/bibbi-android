package com.no5ing.bbibbi.presentation.feature.view.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.CustomDialogPosition
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.customDialogModifier
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getDisabledEmojiResource
import com.no5ing.bbibbi.util.getEmojiResource
import com.no5ing.bbibbi.util.getRealEmojiResource
import com.no5ing.bbibbi.util.getScreenSize
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReactionDialog(
    onTapEmoji: (String) -> Unit,
    onTapRealEmoji: (MemberRealEmoji) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    onDispose: () -> Unit,
    realEmojiMap: Map<String, MemberRealEmoji>,
    isEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    if (isEnabled.value) {
        val coroutineScope = rememberCoroutineScope()
        val navController = LocalNavigateControllerState.current
        var showAnimate by remember {
            mutableStateOf(false)
        }
        var goDispose by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(goDispose) {
            if (goDispose) {
                isEnabled.value = false
            }
        }
        val parentBarPadding = WindowInsets.systemBars.asPaddingValues()
        Dialog(
            onDismissRequest = { showAnimate = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
                window.setWindowAnimations(-1)
            }
            val (width, height) = getScreenSize()
            val swipeableState = rememberSwipeableState(initialValue = 0)
            Box(
                modifier = Modifier
                    .requiredSize(width, height)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        coroutineScope.launch {
                            swipeableState.animateTo(1)
                        }
                        //  showAnimate = false
                    }
            ) {
                LaunchedEffect(Unit) {
                    showAnimate = true
                }
                AnimatedVisibility(
                    showAnimate,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    val point =
                        LocalDensity.current.run { LocalConfiguration.current.screenHeightDp.dp.toPx() }
                    // 위치 to 상태
                    val anchors = mapOf(0f to 0, point to 1)

                    DisposableEffect(Unit) {
                        onDispose {
                            goDispose = true
                        }
                    }
                    LaunchedEffect(swipeableState.currentValue) {
                        if (swipeableState.currentValue == 1) {
                            goDispose = true
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .customDialogModifier(CustomDialogPosition.BOTTOM)
                            .fillMaxWidth()
                            // .fillMaxHeight(0.6f)
                            .clickable(enabled = false) {
                                //COMSUME
                            }
                            .swipeable(
                                state = swipeableState,
                                anchors = anchors,
                                thresholds = { _, _ -> FractionalThreshold(0.4f) },
                                orientation = Orientation.Vertical
                            )
                            .offset { IntOffset(0, swipeableState.offset.value.toInt()) },
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp),
                        color = MaterialTheme.bbibbiScheme.button,
                    ) {
                        Box(
                            modifier = Modifier
                                //.clip(RoundedCornerShape(48.dp))
                                // .background(color = MaterialTheme.bbibbiScheme.button)
                                .padding(vertical = 10.dp, horizontal = 16.dp)
                                .padding(bottom = 50.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 32.dp, height = 4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.bbibbiScheme.backgroundPrimary)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
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
                                Spacer(modifier = Modifier.height(14.dp))
                            }


                        }

                    }

                }

            }
        }
    }
}