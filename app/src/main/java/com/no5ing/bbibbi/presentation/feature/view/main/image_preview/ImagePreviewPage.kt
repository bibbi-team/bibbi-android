package com.no5ing.bbibbi.presentation.feature.view.main.image_preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun ImagePreviewPage(
    imageUrl: String,
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
                    DisposableTopBar(onDispose = onDispose, title = "")
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

@Preview(
    showBackground = true,
    name = "ImagePreviewPage",
    showSystemUi = true
)
@Composable
fun ImagePreviewPagePreview() {
    BBiBBiPreviewSurface {
        ImagePreviewPage(
            imageUrl = "https://picsum.photos/200/300"
        )
    }
}