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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.navigate
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.feature.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.feature.uistate.post.RealEmojiPostReactionUiState
import com.no5ing.bbibbi.util.CustomDialogPosition
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.customDialogModifier
import com.no5ing.bbibbi.util.getEmojiResource
import com.no5ing.bbibbi.util.getRealEmojiResource
import com.no5ing.bbibbi.util.getScreenSize

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReactionListDialog(
    selectedEmoji: PostReactionUiState,
    isEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    myGroup: List<PostReactionUiState>,
) {
    if (isEnabled.value) {
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
            val totalCntMessage = stringResource(id = R.string.emoji_reaction_total, myGroup.size)
            Box(
                modifier = Modifier
                    .requiredSize(width, height)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        showAnimate = false
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
                    val swipeableState = rememberSwipeableState(initialValue = 0)
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
                            .fillMaxHeight(0.6f)
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
                        color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = parentBarPadding.calculateBottomPadding()
                                )
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .size(width = 32.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.bbibbiScheme.button)
                            )
                            Box(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (selectedEmoji is RealEmojiPostReactionUiState) {
                                        Box(
                                            contentAlignment = Alignment.BottomEnd,
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = asyncImagePainter(selectedEmoji.imageUrl),
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
                                                    painter = getRealEmojiResource(emojiName = selectedEmoji.emojiType),
                                                    contentDescription = null, // 필수 param
                                                    modifier = Modifier
                                                        .size(20.dp),
                                                )
                                            }
                                        }
                                    } else {
                                        Image(
                                            painter = getEmojiResource(emojiName = selectedEmoji.emojiType),
                                            contentDescription = null, // 필수 param
                                            modifier = Modifier
                                                .size(width = 42.dp, height = 42.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = totalCntMessage,
                                        color = MaterialTheme.bbibbiScheme.white,
                                        style = MaterialTheme.bbibbiTypo.headTwoBold,
                                    )
                                }
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {

                                items(myGroup.size) {
                                    val item = myGroup[it]
                                    val currentMember = item.member ?: Member.unknown()
                                    Box(Modifier.padding(vertical = 14.dp, horizontal = 20.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            CircleProfileImage(
                                                member = currentMember,
                                                size = 52.dp,
                                                onTap = {
                                                    navController.navigate(
                                                        destination = ProfilePageController,
                                                        path = currentMember.memberId
                                                    )
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = currentMember.name,
                                                    style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                                    color = MaterialTheme.bbibbiScheme.white,
                                                )
                                                if (item.isMe) {
                                                    Text(
                                                        text = stringResource(id = R.string.family_me),
                                                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                                                        color = MaterialTheme.bbibbiScheme.icon,
                                                    )
                                                }
                                            }

                                        }
                                    }

                                }

                            }
                            CTAButton(
                                text = stringResource(id = R.string.close),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .height(56.dp),
                                onClick = {
                                    showAnimate = false
                                }
                            )
                        }
                    }

                }

            }
        }
    }
}