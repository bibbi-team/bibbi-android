package com.no5ing.bbibbi.widget

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private const val TAG = "WidgetImageWorker"
class WidgetImageWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(1, Notification())
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Updating widget...")
            updateImageWidget { prefs ->
                prefs[AppWidget.resultKey] = AppWidget.WIDGET_LOADING
            }
            val force = inputData.getBoolean("force", false)
            withContext(Dispatchers.IO) {
                fetchDetails()
            }.use { apiResult ->
                if (apiResult.isSuccessful) {
                    if (apiResult.code == 200) {
                        val newToken = apiResult.body!!.string()
                        val newWidgetToken = JsonParser.parseString(newToken).asJsonObject
                        updateImageWidget { prefs ->
                            prefs[AppWidget.resultKey] = AppWidget.WIDGET_SUCCESS
                            prefs[AppWidget.imageKey] = loadImage(newWidgetToken.get("postImageUrl").asString, force)
                            prefs[AppWidget.postContentKey] = newWidgetToken.get("postContent").asString
                            prefs[AppWidget.postIdKey] = newWidgetToken.get("postId").asString
                            newWidgetToken?.get("profileImageUrl")?.asString?.let { loadImage(it, force) }?.apply {
                                prefs[AppWidget.profileImageKey] = this
                            } ?: Unit.apply {
                                prefs[AppWidget.userNameKey] = newWidgetToken?.get("authorName")?.asString ?: "?"
                                prefs.remove(AppWidget.profileImageKey)
                            }
                        }
                    } else if (apiResult.code == 204) {
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
            Log.e(TAG, "Error while loading image")
            e.printStackTrace()
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
        val path = context.imageLoader.diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
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

    private fun fetchDetails(): Response {
        val client = createOkHttpClient()
        val widgetRequest = Request.Builder()
            .url(BuildConfig.apiBaseUrl + "/v1/widgets/single-recent-family-post")
            .get()
            .build()
        val response = client.newCall(widgetRequest).execute()
        Log.d(TAG, "[NetworkModule] Invoke widget refresh $response")
        return response
    }

    private fun createOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor(createInterceptor())
        return client
            .build()
    }

    private fun createInterceptor(): Interceptor = Interceptor {
        val request = it.request()
        val accessToken = getAuthTokenFromContext(context)
        val modifiedRequest = with(request) {
            return@with newBuilder()
                .header("Accept", "application/json")
                .header("X-APP-KEY", BuildConfig.appKey)
                .header("X-AUTH-TOKEN", accessToken ?: "")
                .header("X-USER-PLATFORM", "AOS")
                .header("X-USER-ID", "WIDGET")
                .build()
        }

        val response = it.proceed(modifiedRequest)
        Log.d(TAG, "[NetworkModule] Invoke widget refresh")
        response
    }
}