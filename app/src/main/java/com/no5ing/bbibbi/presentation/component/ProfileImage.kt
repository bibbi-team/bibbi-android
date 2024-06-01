package com.no5ing.bbibbi.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun CircleProfileImage(
    modifier: Modifier = Modifier,
    size: Dp,
    member: Member,
    opacity: Float = 1.0f,
    backgroundColor: Color = MaterialTheme.bbibbiScheme.backgroundSecondary,
    onTap: () -> Unit = {},
) {
    CircleProfileImage(
        size = size,
        noImageLetter = member.name.first().toString(),
        imageUrl = member.imageUrl,
        opacity = opacity,
        backgroundColor = backgroundColor,
        onTap = onTap
    )
}

@Composable
fun CircleProfileImage(
    modifier: Modifier = Modifier,
    size: Dp,
    noImageLetter: String,
    imageUrl: String?,
    opacity: Float = 1.0f,
    backgroundColor: Color = MaterialTheme.bbibbiScheme.backgroundSecondary,
    onTap: () -> Unit = {},
) {
    Box {
        if (imageUrl != null) {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable { onTap() },
                alpha = opacity,
            )
        } else {
            Box(
                modifier = Modifier.clickable { onTap() }
            ) {
                Box(
                    modifier = modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.bbibbiScheme
                                .backgroundHover
                                .copy(alpha = opacity)
                        )
                )
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = noImageLetter,
                        fontSize = 28.sp * (size / 90.dp),
                        color = MaterialTheme.bbibbiScheme.white.copy(alpha = opacity),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
