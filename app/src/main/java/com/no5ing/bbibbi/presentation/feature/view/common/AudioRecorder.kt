package com.no5ing.bbibbi.presentation.feature.view.common

import android.content.Context
import android.media.MediaRecorder
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AudioRecorder(
    modifier: Modifier,
    recordingState: MutableState<Boolean>,
    audioRecorder: AudioRecorderImpl
) {
    val context = LocalContext.current
    var isRecording by recordingState
    var amplitude by remember { mutableIntStateOf(0) }

    // 녹음 중에는 주기적으로 음량(최대 진폭)을 업데이트
    LaunchedEffect(isRecording) {
        while (isRecording) {
            amplitude = audioRecorder.getMaxAmplitude()
            delay(100L) // 100ms 주기로 업데이트
        }
    }

    if (isRecording) {
        EqualizerBar(modifier = modifier, amplitude = amplitude)
    }
}

class AudioRecorderImpl {
    private var mediaRecorder: MediaRecorder? = null
    private var recordStartTime = 0L

    fun startRecording(context: Context) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            val file = File(context.cacheDir, "recording.aac")
            setOutputFile(file.absolutePath)
            prepare()
            start()

            recordStartTime = SystemClock.elapsedRealtime()
        }
    }

    fun stopRecording(): Boolean {
        val isRecording = mediaRecorder != null
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        return isRecording
    }

    fun getElapsedRecordingTime(): Long {
        return if (recordStartTime > 0) {
            (SystemClock.elapsedRealtime() - recordStartTime) / 1000 // 초 단위로 반환
        } else {
            0L
        }
    }

    fun getMaxAmplitude(): Int {
        return mediaRecorder?.maxAmplitude ?: 0
    }
}


@Composable
fun EqualizerBar(modifier: Modifier, amplitude: Int) {
    val normalizedAmplitude = (amplitude / 32767f).coerceIn(0f, 1f)
    val bars = remember { mutableStateListOf<Float>() }
    var maxBarSize by remember {
        mutableIntStateOf(10)
    }
    val density = LocalDensity.current

    LaunchedEffect(normalizedAmplitude) {
        bars.add(normalizedAmplitude)
        if (bars.size > maxBarSize) {
            bars.removeFirst()
        }
    }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                maxBarSize = with(density) {
                    (it.size.width / 6.dp.toPx()).toInt() - 2
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(bars.size) { index ->
            val barHeight = 2.dp + (bars[index] * 22).dp
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.bbibbiScheme.mainYellow)
            )
        }

        items((maxBarSize - bars.size).coerceAtLeast(0)) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.bbibbiScheme.gray500)
            )
        }
    }
}