package com.no5ing.bbibbi.presentation.feature.view.common
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.linc.amplituda.Amplituda
import com.linc.amplituda.AmplitudaResult
import com.linc.amplituda.Cache
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun EqualizerWithPlayerAndAmplitude(
    player: ExoPlayer,
    url: String
) {
    val mediaItem = MediaItem.fromUri(url)
    val context = LocalContext.current
    val density = LocalDensity.current

    var calculatedMaxSizeMillis by remember {
        mutableStateOf(0L)
    }
    var maxBarSize by remember { mutableStateOf(0) }
    var amplitudes by remember {
        mutableStateOf(listOf(0.0f))
    }
    var timeRemainingMillis by remember {
        mutableStateOf(0L)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    // Start playing when the component is launched
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            Amplituda(context)
                .processAudio(url, Cache.withParams(Cache.REUSE))
                .get().apply {
                    amplitudes = this.amplitudesAsList()
                        .map {
                            (it / 64.0f).coerceIn(0.0f, 1.0f)
                        }
                    calculatedMaxSizeMillis =
                        this.getAudioDuration(AmplitudaResult.DurationUnit.MILLIS)
                    timeRemainingMillis = calculatedMaxSizeMillis
                }

        }
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while(true) {
                if (player.currentMediaItem != mediaItem) {
                    isPlaying = false
                    break
                }
                timeRemainingMillis =
                    calculatedMaxSizeMillis
                        .coerceAtMost(player.duration - player.currentPosition)
                        .coerceAtLeast(0)
                delay(150L)
            }
        }
    }

    fun tapPlay() {
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        isPlaying = true
    }

    fun tapPause() {
        player.pause()
        isPlaying = false
    }

    val dx = ((calculatedMaxSizeMillis - timeRemainingMillis) / (calculatedMaxSizeMillis * 1.0f)).coerceIn(0f, 1f)
    val globalCurrentIndex = (dx * amplitudes.size).toInt()
    val halfWindow = maxBarSize / 2
    var startIndex = (globalCurrentIndex - halfWindow).coerceAtLeast(0)
    if (startIndex + maxBarSize > amplitudes.size) {
        startIndex = amplitudes.size - maxBarSize
    }
    startIndex = startIndex.coerceAtLeast(0)
    val scopedAmplitudes = amplitudes.subList(startIndex, (startIndex + maxBarSize).coerceAtMost(amplitudes.size))
    val highlightIndex = if (scopedAmplitudes.isNotEmpty()) {
        (globalCurrentIndex - startIndex).coerceIn(0, scopedAmplitudes.size - 1)
    } else {
        0
    }
    

    Row(
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.bbibbiScheme.backgroundSecondary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isPlaying) {
            Icon(
                painter = painterResource(id = R.drawable.pause_button),
                contentDescription = null,
                modifier = Modifier
                    .size(12.dp)
                    .clickable { tapPause() },
                tint = MaterialTheme.bbibbiScheme.mainYellow
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.play_button),
                contentDescription = null,
                modifier = Modifier
                    .size(12.dp)
                    .clickable { tapPlay() },
                tint = MaterialTheme.bbibbiScheme.mainYellow
            )
        }
        LazyRow(
            modifier = Modifier
                .weight(1.0f)
                .onGloballyPositioned {
                    maxBarSize = with(density) {
                        (it.size.width / 6.dp.toPx()).toInt()
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(scopedAmplitudes.size) { index ->
                val barHeight = 2.dp + (scopedAmplitudes[index] * 22).dp
                val barColor = if (index < highlightIndex) {
                    MaterialTheme.bbibbiScheme.mainYellow
                } else {
                    MaterialTheme.bbibbiScheme.gray500
                }
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(100.dp))
                        .background(barColor)
                )
            }

            items((maxBarSize - amplitudes.size).coerceAtLeast(0)) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.bbibbiScheme.gray600)
                )
            }
        }
        Text(
            text = formatTime((timeRemainingMillis / 1000).toInt()),
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
            color = MaterialTheme.bbibbiScheme.gray500,
        )
    }

}