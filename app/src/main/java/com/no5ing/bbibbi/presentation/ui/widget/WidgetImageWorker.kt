package com.no5ing.bbibbi.presentation.ui.widget

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.gson.Gson
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.model.widget.WidgetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.time.Duration


class WidgetImageWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = WidgetImageWorker::class.java.simpleName

        fun enqueue(context: Context, glanceId: GlanceId, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = OneTimeWorkRequestBuilder<WidgetImageWorker>().apply {
                addTag(glanceId.toString())
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putBoolean("force", force)
                        .build()
                )
            }
            val workPolicy = if (force) {
                ExistingWorkPolicy.REPLACE
            } else {
                ExistingWorkPolicy.KEEP
            }

            manager.enqueueUniqueWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )

            // Temporary workaround to avoid WM provider to disable itself and trigger an
            // app widget update
            manager.enqueueUniqueWork(
                "$uniqueWorkName-workaround",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<WidgetImageWorker>().apply {
                    setInitialDelay(Duration.ofDays(365))
                }.build()
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Updating widget...")
            updateImageWidget { prefs ->
                prefs[AppWidget.resultKey] = AppWidget.WIDGET_LOADING
            }
            val force = inputData.getBoolean("force", false)
            withContext(Dispatchers.IO) {
                fetchDetails()
            }.use { apiResult ->
                if(apiResult.isSuccessful) {
                    if(apiResult.code == 200) {
                        val newToken = apiResult.body!!.string()
                        val newWidget = Gson().fromJson(newToken, WidgetResult::class.java)
                        updateImageWidget { prefs ->
                            prefs[AppWidget.resultKey] = AppWidget.WIDGET_SUCCESS
                            prefs[AppWidget.imageKey] = loadImage(newWidget.postImageUrl, force)
                            prefs[AppWidget.postContentKey] = newWidget.postContent
                            prefs[AppWidget.profileImageKey] = loadImage(newWidget.profileImageUrl, force)
                        }
                    } else if(apiResult.code == 204) {
                        updateImageWidget { prefs ->
                            prefs[AppWidget.resultKey] = AppWidget.WIDGET_NO_RESULT
                        }
                    }
                } else {
                    updateImageWidget { prefs ->
                        prefs[AppWidget.resultKey] = AppWidget.WIDGET_UNAUTHENTICATED
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error while loading image")
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun updateImageWidget(updateState: suspend (MutablePreferences) -> Unit) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(AppWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId, updateState)
        }
        AppWidget().updateAll(context)
    }

    /**
     * Use Coil and Picsum Photos to randomly load images into the cache based on the provided
     * size. This method returns the path of the cached image, which you can send to the widget.
     */
    @OptIn(ExperimentalCoilApi::class)
    private suspend fun loadImage(imageUrl: String, force: Boolean): String {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        // Request the image to be loaded and throw error if it failed
        with(context.imageLoader) {
            if (force) {
                diskCache?.remove(imageUrl)
                memoryCache?.remove(MemoryCache.Key(imageUrl))
            }
            val result = execute(request)
            if (result is ErrorResult) {
                throw result.throwable
            }
        }

        // Get the path of the loaded image from DiskCache.
        val path = context.imageLoader.diskCache?.get(imageUrl)?.use { snapshot ->
            val imageFile = snapshot.data.toFile()

            // Use the FileProvider to create a content URI
            val contentUri = getUriForFile(
                context,
                "com.no5ing.bbibbi.fileprovider",
                imageFile
            )

            // Find the current launcher everytime to ensure it has read permissions
            val resolveInfo = context.packageManager.resolveActivity(
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) },
                PackageManager.MATCH_DEFAULT_ONLY
            )
            val launcherName = resolveInfo?.activityInfo?.packageName
            if (launcherName != null) {
                context.grantUriPermission(
                    launcherName,
                    contentUri,
                    FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                )
            }

            // return the path
            contentUri.toString()
        }
        return requireNotNull(path) {
            "Couldn't find cached file"
        }
    }

    private suspend fun fetchDetails(): Response {
        val client = createOkHttpClient()
        val widgetRequest = Request.Builder()
            .url(BuildConfig.apiBaseUrl + "v1/widgets/single-recent-family-post")
            .get()
            .build()
        val response = client.newCall(widgetRequest).execute()
        Timber.d("[NetworkModule] Invoke widget refresh $response")
        return response
    }

    private fun createOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor(createInterceptor())
        return client
            .build()
    }

    private fun createInterceptor(): Interceptor = Interceptor  {
        val request = it.request()
        val localDataStorage = LocalDataStorage(context)
        val modifiedRequest = with(request) {
            return@with newBuilder()
                .header("Accept", "application/json")
                .header("X-APP-KEY", BuildConfig.appKey)
                .header("X-APP-VERSION", BuildConfig.VERSION_NAME)
                .header("X-AUTH-TOKEN", localDataStorage.getAuthTokens()?.accessToken ?: "")
                .header("X-USER-PLATFORM", "AOS")
                .header("X-USER-ID", localDataStorage.getMe()?.memberId ?: "WIDGET")
                .build()
        }

        val response = it.proceed(modifiedRequest)
        Timber.d("[NetworkModule] Invoke widget refresh")
        response
    }
}