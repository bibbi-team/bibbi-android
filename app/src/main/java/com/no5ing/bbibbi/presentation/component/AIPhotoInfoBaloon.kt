package com.no5ing.bbibbi.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor

@Composable
fun AIPhotoInfoBaloon() {
    val balloonColor = MaterialTheme.bbibbiScheme.button
    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setMarginTop(12)
        setPaddingVertical(10)
        setPaddingHorizontal(16)
        setMarginHorizontal(12)
        setCornerRadius(12f)
        setBackgroundColor(balloonColor)
        // setBackgroundColorResource(balloonColor)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }
    Balloon(
        builder = builder,
        balloonContent = {
            Text(
                text = "추석이 있는 한 달 동안만\n열리는 AI 가족사진관 이벤트입니다",
                textAlign = TextAlign.Center,
                color = MaterialTheme.bbibbiScheme.white,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
        }
    ) { baloon->
        Icon(
            painter = painterResource(id = R.drawable.warning_circle_icon),
            tint = MaterialTheme.bbibbiScheme.textSecondary,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .clickable { baloon.showAlignBottom() }
        )
    }
}