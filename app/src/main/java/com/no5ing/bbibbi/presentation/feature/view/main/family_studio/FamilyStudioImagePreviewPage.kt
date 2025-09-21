package com.no5ing.bbibbi.presentation.feature.view.main.family_studio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun FamilyStudioImagePreviewPage(
    imageUrl: String,
    authorImageUrl: String,
    authorName: String,
    date: String,
    onDispose: () -> Unit = {},
) {
    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Box {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.1f
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    PreviewPostViewTopBar(
                        onTap = onDispose,
                        onDispose = onDispose,
                        date = date,
                        profileImageUrl = authorImageUrl,
                        nickname = authorName
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                    AsyncImage(
                        model = asyncImagePainter(source = imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.0f)
                            .clip(RoundedCornerShape(48.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewPostViewTopBar(
    onTap: () -> Unit,
    onDispose: () -> Unit,
    date: String,
    profileImageUrl: String,
    nickname: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.return_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(52.dp)
                .clickable { onDispose() }
        )
        CircleProfileImage(
            noImageLetter = nickname.first().toString(),
            imageUrl = profileImageUrl,
            size = 40.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = nickname,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
            )
            Text(
                text = date.replace("+", " "),
                color = MaterialTheme.bbibbiScheme.icon,
                fontSize = 12.sp,
            )

        }
    }
}

//@Preview(
//    showBackground = true,
//    name = "ImagePreviewPage",
//    showSystemUi = true
//)
//@Composable
//fun ImagePreviewPagePreview() {
//    BBiBBiPreviewSurface {
//        FamilyStudioImagePreviewPage(
//            imageUrl = "https://picsum.photos/200/300"
//        )
//    }
//}