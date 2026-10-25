package com.no5ing.bbibbi.presentation.feature.view_model

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.coroutineContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface FamilyPhotoDownloadState {
    data object Idle : FamilyPhotoDownloadState
    data class Downloading(val downloadedBytes: Long, val totalBytes: Long?) :
        FamilyPhotoDownloadState

    data object Success : FamilyPhotoDownloadState
    data object Failed : FamilyPhotoDownloadState
}

/**
 * 서비스 종료 안내 팝업에서 가족 사진 전체를 ZIP으로 내려받는다.
 * 서버가 스트리밍으로 내려주므로 Retrofit/Sandwich 대신 OkHttp로 직접 받아 SAF 파일에 쓴다.
 */
@HiltViewModel
class FamilyPhotoDownloadViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    okHttpClient: OkHttpClient,
) : ViewModel() {
    // 사진이 많으면 서버가 압축하는 동안 응답이 늦을 수 있어 타임아웃을 넉넉히 둔다.
    private val downloadClient = okHttpClient.newBuilder()
        .readTimeout(5, TimeUnit.MINUTES)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val _state = MutableStateFlow<FamilyPhotoDownloadState>(FamilyPhotoDownloadState.Idle)
    val state: StateFlow<FamilyPhotoDownloadState> = _state.asStateFlow()

    private var downloadJob: Job? = null

    @Volatile
    private var currentCall: Call? = null

    fun shouldShowPopup(): Boolean = !popupShownThisLaunch

    fun markPopupShown() {
        popupShownThisLaunch = true
    }

    fun defaultFileName(): String =
        "bibbi_family_photos_${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.zip"

    fun download(targetUri: Uri) {
        if (downloadJob?.isActive == true) return
        _state.value = FamilyPhotoDownloadState.Downloading(0, null)
        downloadJob = viewModelScope.launch {
            try {
                val succeeded = withContext(Dispatchers.IO) { writeZipTo(targetUri) }
                if (!succeeded) withContext(Dispatchers.IO) { deletePartialFile(targetUri) }
                _state.value =
                    if (succeeded) FamilyPhotoDownloadState.Success else FamilyPhotoDownloadState.Failed
            } catch (e: CancellationException) {
                withContext(NonCancellable + Dispatchers.IO) { deletePartialFile(targetUri) }
                throw e
            }
        }
    }

    fun cancel() {
        // 블로킹 read를 즉시 끊기 위해 OkHttp call도 함께 취소한다.
        currentCall?.cancel()
        downloadJob?.cancel()
        _state.value = FamilyPhotoDownloadState.Idle
    }

    override fun onCleared() {
        currentCall?.cancel()
        super.onCleared()
    }

    fun resetState() {
        _state.value = FamilyPhotoDownloadState.Idle
    }

    private suspend fun writeZipTo(targetUri: Uri): Boolean {
        val request = Request.Builder()
            .url(BuildConfig.apiBaseUrl + "/v1/family-photos/download")
            .header("Accept", "application/zip, */*")
            .get()
            .build()
        val call = downloadClient.newCall(request)
        currentCall = call
        return try {
            call.execute().use { response ->
                if (!response.isSuccessful) {
                    Timber.w("[FamilyPhotoDownload] HTTP %d", response.code)
                    return false
                }
                val body = response.body ?: return false
                val totalBytes = body.contentLength().takeIf { it > 0 }
                val output = context.contentResolver.openOutputStream(targetUri, "w")
                    ?: return false
                output.use { out ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE * 8)
                        var downloaded = 0L
                        var lastReported = 0L
                        while (true) {
                            coroutineContext.ensureActive()
                            val read = input.read(buffer)
                            if (read == -1) break
                            out.write(buffer, 0, read)
                            downloaded += read
                            // UI 갱신은 256KB마다 한 번만
                            if (downloaded - lastReported >= 256 * 1024) {
                                lastReported = downloaded
                                _state.value =
                                    FamilyPhotoDownloadState.Downloading(downloaded, totalBytes)
                            }
                        }
                        out.flush()
                        downloaded > 0
                    }
                }
            }
        } catch (e: IOException) {
            Timber.w(e, "[FamilyPhotoDownload] download failed")
            false
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "[FamilyPhotoDownload] unexpected error")
            false
        } finally {
            currentCall = null
        }
    }

    private fun deletePartialFile(uri: Uri) {
        try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            Timber.w(e, "[FamilyPhotoDownload] failed to delete partial file")
        }
    }

    companion object {
        // 프로세스(앱 실행) 단위로 한 번만 보여주기 위해 static으로 둔다.
        @Volatile
        private var popupShownThisLaunch = false
    }
}
