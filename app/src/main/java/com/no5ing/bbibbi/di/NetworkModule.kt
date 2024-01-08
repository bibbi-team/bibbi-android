package com.no5ing.bbibbi.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.util.ZonedDateTimeAdapter
import com.no5ing.bbibbi.util.forceRestart
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val timeout_read = 10L
    private const val timeout_connect = 10L
    private const val timeout_write = 10L

    val requireUpdateState = MutableStateFlow(false)
    val requireTokenInvalidRestart = MutableStateFlow(false)

    @Provides
    @Singleton
    fun provideInterceptor(
        localDataStorage: LocalDataStorage,
    ): Interceptor = Interceptor {
        val request = it.request()
        val start = System.currentTimeMillis()
        val modifiedRequest = with(request) {
            val builder = newBuilder()
            val currentToken = localDataStorage.getAuthTokens()
            builder
                .header("Accept", "application/json")
                .header("X-APP-KEY", BuildConfig.appKey)
                .header("X-APP-VERSION", BuildConfig.VERSION_NAME)
            if (currentToken != null) {
                builder
                    .header("X-AUTH-TOKEN", currentToken.accessToken)

                val me = localDataStorage.getMe()
                if (me != null) {
                    builder
                        .header("X-USER-ID", me.memberId)
                        .header("X-USER-PLATFORM", "AOS")
                }
            }

            builder.build()
        }

        val response = it.proceed(modifiedRequest)
        val elapsed = System.currentTimeMillis() - start
        Timber.d("[NetworkModule] ${request.method} ${request.url} ${response.code} ${elapsed}ms")
        if (response.code == 426) {
            requireUpdateState.value = true
        }
        response
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        localDataStorage: LocalDataStorage,
        context: Context,
    ): Authenticator {
        val authenticatorClient = createOkHttpClient(null, null)
        return Authenticator { route, response ->
            if (response.code == 401) {
                Timber.d("[NetworkModule] Refresh tokens with Authenticator")
                val previousToken = localDataStorage.getAuthTokens() ?: return@Authenticator null
                val refreshRequest = Request.Builder()
                    .url(BuildConfig.apiBaseUrl + "v1/auth/refresh")
                    .post(
                        "{\"refreshToken\": \"${previousToken.refreshToken}\"}"
                            .toRequestBody("application/json".toMediaType())
                    )
                    .build()
                kotlin.runCatching {
                    val refreshResponse = authenticatorClient.newCall(refreshRequest).execute()
                    if (refreshResponse.isSuccessful) {
                        val newToken = refreshResponse.body!!.string()
                        val newTokenObject = Gson().fromJson(newToken, AuthResult::class.java)

                        localDataStorage.setAuthTokens(newTokenObject)
                        return@Authenticator response.request
                            .newBuilder()
                            .removeHeader("X-AUTH-TOKEN")
                            .addHeader("X-AUTH-TOKEN", newTokenObject.accessToken)
                            .build()
                    } else throw RuntimeException()
                }.onFailure {
                    localDataStorage.logOut()
                    requireTokenInvalidRestart.value = true
                    context.forceRestart()
                }
            }
            null
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: Interceptor,
        authenticator: Authenticator,
    ): OkHttpClient {
        return createOkHttpClient(interceptor, authenticator)
    }

    private fun createOkHttpClient(
        interceptor: Interceptor?,
        authenticator: Authenticator?,
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
        if (authenticator != null) client.authenticator(authenticator)
        if (interceptor != null) client.addInterceptor(interceptor)
//        val httpLoggingInterceptor =
//            HttpLoggingInterceptor { message -> Timber.d("%s", message) }
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return client
            // .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout_connect, TimeUnit.SECONDS)
            .readTimeout(timeout_read, TimeUnit.SECONDS)
            .writeTimeout(timeout_write, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        SandwichInitializer.sandwichTimeout = Timeout().timeout(20, TimeUnit.SECONDS)
        return Retrofit.Builder()
            .baseUrl(BuildConfig.apiBaseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapter(
                        ZonedDateTime::class.java,
                        ZonedDateTimeAdapter()
                    )
                        .create()
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRestFamilyApi(retrofit: Retrofit): RestAPI.FamilyApi {
        return retrofit.create(RestAPI.FamilyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRestMemberApi(retrofit: Retrofit): RestAPI.MemberApi {
        return retrofit.create(RestAPI.MemberApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRestPostApi(retrofit: Retrofit): RestAPI.PostApi {
        return retrofit.create(RestAPI.PostApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRestAuthApi(retrofit: Retrofit): RestAPI.AuthApi {
        return retrofit.create(RestAPI.AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRestLinkApi(retrofit: Retrofit): RestAPI.LinkApi {
        return retrofit.create(RestAPI.LinkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRestApi(
        familyApi: RestAPI.FamilyApi,
        memberApi: RestAPI.MemberApi,
        postApi: RestAPI.PostApi,
        authApi: RestAPI.AuthApi,
        linkApi: RestAPI.LinkApi,
    ): RestAPI {
        return object : RestAPI {
            override fun getFamilyApi(): RestAPI.FamilyApi {
                return familyApi
            }

            override fun getMemberApi(): RestAPI.MemberApi {
                return memberApi
            }

            override fun getPostApi(): RestAPI.PostApi {
                return postApi
            }

            override fun getAuthApi(): RestAPI.AuthApi {
                return authApi
            }

            override fun getLinkApi(): RestAPI.LinkApi {
                return linkApi
            }
        }
    }
}