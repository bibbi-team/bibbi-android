package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.view.MainPagePickerModel
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.component.button.CameraCaptureButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun BoxScope.HomePageSurvivalUploadButton(
    isLoading: Boolean = false,
    isUploadAbleTime: Boolean = true,
    isAlreadyUploaded: Boolean = false,
    pickers: List<MainPagePickerModel> = emptyList(),
    onTap: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(vertical = 15.dp)
            .systemBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if(pickers.isNotEmpty() && isUploadAbleTime && !isAlreadyUploaded && !isLoading) {
                    WaitingMembersPop(
                        pickers = pickers,
                    )
                } else {
                    UploadHelperPop(
                        text =
                        if (isUploadAbleTime && !isAlreadyUploaded)
                            stringResource(id = R.string.home_one_image_per_day)
                        else if (isAlreadyUploaded)
                            stringResource(id = R.string.home_already_uploaded_today)
                        else
                            stringResource(id = R.string.home_not_camera_time)
                    )
                }
                CameraCaptureButton(
                    onClick = onTap,
                    isCapturing = !isUploadAbleTime || isAlreadyUploaded,
                )
            }
        }
    }

}

@Composable
fun BoxScope.HomePageMissionUploadButton(
    isLoading: Boolean,
    isMeUploadedToday: Boolean,
    isMissionUnlocked: Boolean,
    isMeMissionUploaded: Boolean,
    onTap: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(vertical = 15.dp)
            .systemBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                UploadHelperPop(
                    text =
                    if(!isMeUploadedToday)
                        "생존신고 후 미션 사진을 올릴 수 있어요"
                    else if (!isMissionUnlocked)
                        "아직 미션 사진을 찍을 수 없어요"
                    else if (isMeMissionUploaded)
                        "오늘의 미션은 완료되었어요"
                    else
                        "미션 사진을 찍으러 가볼까요?"
                )
                CameraCaptureButton(
                    onClick = onTap,
                    isCapturing = !(!isMeMissionUploaded && isMissionUnlocked && isMeUploadedToday),
                    ignoreDisabledState = true,
                )
            }
        }
    }

}


@Composable
fun UploadHelperPop(
    text: String,
) {
    Box(modifier = Modifier.padding(bottom = 30.dp)) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.bbibbiScheme.button, shape = RoundedCornerShape(6.dp))
                .padding(
                    vertical = 10.dp,
                    horizontal = 14.dp
                )
        ) {
            Text(
                text = text,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                color = MaterialTheme.bbibbiScheme.white,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 24.dp)
                .size(24.dp, 24.dp)
        ) {
            val surfaceColor = MaterialTheme.bbibbiScheme.button
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val rect = Rect(Offset.Zero, size)
                val trianglePath = Path().apply {
                    moveTo(rect.topLeft.x, rect.topLeft.y)
                    lineTo(rect.topRight.x, rect.topRight.y)
                    lineTo(rect.bottomCenter.x, rect.bottomCenter.y)
                    close()
                }

                drawIntoCanvas { canvas ->
                    canvas.drawOutline(
                        outline = Outline.Generic(trianglePath),
                        paint = Paint().apply {
                            color = surfaceColor
                            pathEffect = PathEffect.cornerPathEffect(rect.maxDimension / 5)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WaitingMembersPop(
    pickers: List<MainPagePickerModel> = emptyList(),
) {
    Box(modifier = Modifier.padding(bottom = 30.dp)) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.bbibbiScheme.mainYellow,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    vertical = 10.dp,
                    horizontal = 14.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val pickersShattered = pickers.take(3)
                Row {
                    Box {
                        pickersShattered.reversed().forEachIndexed { rawIdx, it ->
                            MiniCircledIcon(
                                noImageLetter = it.displayName.first().toString(),
                                imageUrl = it.imageUrl,
                                modifier = Modifier.offset(
                                    x = 16.dp * (pickersShattered.size - 1 - rawIdx)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp.times(pickersShattered.size - 1)))
                }
                if(pickers.size == 1) {
                    Text(
                        text = "${pickers.first().displayName}님이 기다리고 있어요",
                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                        color = MaterialTheme.bbibbiScheme.backgroundHover,
                    )
                } else {
                    Text(
                        text = "${pickers.first().displayName}님이 외 ${pickers.size - 1}명이 기다리고 있어요",
                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                        color = MaterialTheme.bbibbiScheme.backgroundHover,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 24.dp)
                .size(24.dp, 24.dp)
        ) {
            val surfaceColor = MaterialTheme.bbibbiScheme.mainYellow
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val rect = Rect(Offset.Zero, size)
                val trianglePath = Path().apply {
                    moveTo(rect.topLeft.x, rect.topLeft.y)
                    lineTo(rect.topRight.x, rect.topRight.y)
                    lineTo(rect.bottomCenter.x, rect.bottomCenter.y)
                    close()
                }

                drawIntoCanvas { canvas ->
                    canvas.drawOutline(
                        outline = Outline.Generic(trianglePath),
                        paint = Paint().apply {
                            color = surfaceColor
                            pathEffect = PathEffect.cornerPathEffect(rect.maxDimension / 5)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MiniCircledIcon(
    modifier: Modifier = Modifier,
    noImageLetter: String,
    imageUrl: String?,
    onTap: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.bbibbiScheme.mainYellow, CircleShape)
        ) {

        }
        if (imageUrl != null) {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { onTap() },
            )
        } else {
            Box(
                modifier = Modifier.clickable { onTap() },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.bbibbiScheme
                                .backgroundHover,
                            CircleShape
                        )
                )
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = noImageLetter,
                        fontSize = 8.sp,
                        color = MaterialTheme.bbibbiScheme.white,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
