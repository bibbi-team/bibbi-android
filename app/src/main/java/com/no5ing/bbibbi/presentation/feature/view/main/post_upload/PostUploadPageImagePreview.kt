package com.no5ing.bbibbi.presentation.feature.view.main.post_upload

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.toCodePointList

@Composable
fun PostUploadPageImagePreview(
    previewImgUrl: Uri?,
    imageTextState: State<String>,
    onTapImageTextButton: () -> Unit = {},
) {
    val imageText by imageTextState
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp))
                        .background(MaterialTheme.bbibbiScheme.backgroundHover),
                    painter = rememberAsyncImagePainter(model = previewImgUrl),
                    contentDescription = null,
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clickable {
                    onTapImageTextButton()
                }
        ) {
            if (imageText.isEmpty()) {
                Image(
                    modifier = Modifier
                        .size(height = 41.dp, width = 36.dp),
                    painter = painterResource(id = R.drawable.textbox_icon),
                    contentDescription = null,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    imageText.toCodePointList().forEach { character ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .size(width = 28.dp, height = 41.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = character,
                                color = MaterialTheme.bbibbiScheme.white,
                                style = MaterialTheme.bbibbiTypo.headTwoBold,
                            )
                        }
                    }

                }
            }
        }

    }
}