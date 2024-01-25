package com.no5ing.bbibbi.presentation.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.text.TextPaint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.no5ing.bbibbi.MainActivity
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.util.codePointLength
import com.no5ing.bbibbi.util.randomBoolean
import com.no5ing.bbibbi.util.toCodePointList
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.time.Duration
import kotlin.streams.toList

class AppWidget: GlanceAppWidget() {
    companion object {
        const val WIDGET_UNAUTHENTICATED = "unauthenticated"
        const val WIDGET_SUCCESS = "success"
        const val WIDGET_NO_RESULT = "success_empty"
        const val WIDGET_LOADING = "loading"

        const val WIDGET_WORKER_NAME = "bbibbi_widget_worker"

        val resultKey = stringPreferencesKey("result")

        val imageKey = stringPreferencesKey("postImgUrl")
        val userNameKey = stringPreferencesKey("nickName")
        val profileImageKey = stringPreferencesKey("profileImgUrl")
        val postContentKey = stringPreferencesKey("postContent")
    }

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(AppWidget::class.java)
        if (glanceIds.isEmpty()) {
            Timber.d("Cancel Widget Schedule")
            WorkManager.getInstance(context).cancelUniqueWork(WIDGET_WORKER_NAME )
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        coroutineScope {
            Timber.d("Start Widget Schedule")
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WIDGET_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequest.Builder(
                    WidgetImageWorker::class.java,
                    Duration.ofMinutes(15),
                )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .build()
            )
            provideContent {
                WidgetBody()
            }
        }
    }

    @Composable
    fun WidgetBody() {
        val size = LocalSize.current
        val aspectSize = if (size.width > size.height) size.height else size.width
        val isSmallSize = aspectSize < 200.dp
        val result = currentState(resultKey)
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = GlanceModifier
                    .size(width = aspectSize, height = aspectSize)
                    .background(Color(0xFF262626))
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                when (result) {
                    WIDGET_SUCCESS -> {
                        ImagePreviewBox(size, isSmallSize)
                    }

                    WIDGET_LOADING, null -> {
                        Box(
                            modifier = GlanceModifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ColorProvider(Color.White)
                            )
                        }
                    }

                    else -> {
                        EmptyBox(isSmallSize)
                    }
                }
            }
        }
    }

    private fun getImageProvider(path: String): ImageProvider {
        if (path.startsWith("content://")) {
            return ImageProvider(path.toUri())
        }
        val bitmap = BitmapFactory.decodeFile(path)
        return ImageProvider(bitmap)
    }

    @Composable
    fun ImagePreviewBox(size: DpSize, isSmallSize: Boolean) {
        val postImagePath = currentState(imageKey)!!
        val profileImagePath = currentState(profileImageKey)
        val postContent = currentState(postContentKey)!!
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
            ) {
                Image(
                    provider = getImageProvider(postImagePath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(if(isSmallSize) 13.dp else 18.dp),
                contentAlignment = Alignment.TopStart
            ) {
                val nameIconSize = if(isSmallSize) 32.dp else 48.dp
                val iconSize = if(isSmallSize) 29.6.dp else 44.dp
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(nameIconSize)
                            .background(Color.White)
                            .cornerRadius(nameIconSize)
                    ) {}
                    Box(
                        modifier = GlanceModifier
                            .size(iconSize)
                            .background(
                                if (profileImagePath != null) Color.Transparent else Color(
                                    0xFF3F3F43
                                )
                            )
                            .cornerRadius(iconSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (profileImagePath != null) {
                            Image(
                                provider = getImageProvider(profileImagePath),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = GlanceModifier
                                    .fillMaxSize()
                            )
                        } else {
                            val nickName = (currentState(userNameKey) ?: "?").first()
                            Text(
                                text = nickName.toString(),
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if(isSmallSize) 14.sp else 18.sp,
                                )
                            )
                        }

                    }
                }


            }
            Box(
                modifier = GlanceModifier
                    .padding(if(isSmallSize) 16.dp else 22.dp)
            ) {
                Column(
                    verticalAlignment = Alignment.Bottom,
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxWidth()
                    ) {
                        val lastIndex = (postContent.codePointLength() - 1).toInt()
                        postContent.toCodePointList().forEachIndexed { index, character ->
                            Row {
                                Box(
                                    modifier = GlanceModifier
                                        .cornerRadius(if(isSmallSize) 5.dp else 10.dp)
                                        .padding(vertical = if(isSmallSize) 4.dp else 8.dp, horizontal = if(isSmallSize) 3.dp else 6.dp)
                                        .background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = character.toString(),
                                        style = TextStyle(
                                            color = ColorProvider(Color.White),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = if(isSmallSize) 12.sp else 18.sp,
                                        )
                                    )
                                }
                                if (index != lastIndex) {
                                    Box(GlanceModifier.width(width = if(isSmallSize) 1.5.dp else 2.dp)) {}
                                }
                            }

                        }

                    }
                }
            }


        }

    }

    @Composable
    fun EmptyBox(
        isSmallSize: Boolean,
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                val imageResource = if (!isSmallSize)
                    if(randomBoolean()) R.drawable.widget_large else R.drawable.widget_large_green
                else
                    if(randomBoolean()) R.drawable.widget_small else R.drawable.widget_small_green
                Image(
                    provider = AndroidResourceImageProvider(imageResource),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
        }
    }
}

val Float.toPx get() = this * Resources.getSystem().displayMetrics.density

fun autoSizeFont(text: String, availableWidthInPx: Int, fontSize: Float = 30.0F): TextUnit {
    val bounds = Rect()
    val paint = TextPaint()
    var fontSizeReturn = fontSize

    val scale = Resources.getSystem().configuration.fontScale

    while (true) {
        paint.textSize =
            (fontSizeReturn * Resources.getSystem().displayMetrics.density) //Try fontsize as sp
        paint.getTextBounds(text, 0, text.length, bounds) //In pixels
        if (availableWidthInPx >= bounds.width()) {
            fontSizeReturn /= scale //Remove scalefactor before returning fontSize
            return fontSizeReturn.sp
        } else {
            fontSizeReturn *= 0.95F //Did not fit, try 5% off
        }
        if (fontSizeReturn <= 1.0F) break
    }

    return TextUnit.Unspecified
}

fun autoSizeBox(
    text: String,
    availableWidthInPx: Int,
    fontSize: Float = 30.0F,
    itemPadding: Int,
    textPadding: Int
): TextUnit {
    val bounds = Rect()
    val paint = TextPaint()
    var fontSizeReturn = fontSize

    val scale = Resources.getSystem().configuration.fontScale
    val itemPaddingTotal = (text.length - 1) * itemPadding
    val textPaddingTotal = text.length * (textPadding * 2)
    val paddingTotal = itemPaddingTotal + textPaddingTotal

    while (true) {
        paint.textSize =
            (fontSizeReturn * Resources.getSystem().displayMetrics.density) //Try fontsize as sp
        paint.getTextBounds(text, 0, text.length, bounds) //In pixels
        if (availableWidthInPx >= bounds.width() + paddingTotal) {
            fontSizeReturn /= scale //Remove scalefactor before returning fontSize
            return fontSizeReturn.sp
        } else {
            fontSizeReturn *= 0.95F //Did not fit, try 5% off
        }
        if (fontSizeReturn <= 1.0F) break
    }

    return TextUnit.Unspecified
}