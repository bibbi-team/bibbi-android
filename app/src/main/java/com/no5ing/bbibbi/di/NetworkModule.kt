package com.no5ing.bbibbi.di

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.google.gson.Gson
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Authenticator
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import timber.log.Timber
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
        sessionModule: SessionModule,
    ): Interceptor = Interceptor {
        val request = it.request()
        val start = System.currentTimeMillis()
        val modifiedRequest = with(request) {
            val builder = newBuilder()
            val sessionState = sessionModule.sessionState.value
            builder
                .header("Accept", "application/json")
                .header("X-APP-KEY", BuildConfig.appKey)
                .header("X-APP-VERSION", BuildConfig.VERSION_NAME)
                .header("X-USER-PLATFORM", "AOS")

            if (sessionState.isLoggedIn()) {
                builder
                    .header("X-AUTH-TOKEN", sessionState.apiToken.accessToken)
                    .header("X-USER-ID", sessionState.memberId)
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
        sessionModule: SessionModule,
        context: Context,
    ): Authenticator {
        val authenticatorClient = createOkHttpClient(null, null)
        return Authenticator { route, response ->
            if (response.code == 401) {
                Timber.d("[NetworkModule] Refresh tokens with Authenticator")
                val currentSession = sessionModule.sessionState.value
                if (!currentSession.isLoggedIn()) return@Authenticator null

                val previousApiToken = currentSession.apiToken
                val headers = Headers.headersOf(
                    "Accept", "application/json",
                    "X-APP-KEY", BuildConfig.appKey,
                    "X-APP-VERSION", BuildConfig.VERSION_NAME,
                    "X-USER-PLATFORM", "AOS",
                    "X-USER-ID", currentSession.memberId,
                )
                val refreshRequest = Request.Builder()
                    .url(BuildConfig.apiBaseUrl + "v1/auth/refresh")
                    .headers(headers)
                    .post(
                        "{\"refreshToken\": \"${previousApiToken.refreshToken}\"}"
                            .toRequestBody("application/json".toMediaType())
                    )
                    .build()
                kotlin.runCatching {
                    authenticatorClient.newCall(refreshRequest).execute().use { refreshResponse ->
                        if (refreshResponse.isSuccessful) {
                            val newToken = refreshResponse.body!!.string()
                            val newTokenObject = Gson().fromJson(newToken, AuthResult::class.java)

                            sessionModule.onRefreshToken(newTokenObject)
                            return@Authenticator response.request
                                .newBuilder()
                                .removeHeader("X-AUTH-TOKEN")
                                .addHeader("X-AUTH-TOKEN", newTokenObject.accessToken)
                                .build()
                        } else throw RuntimeException()
                    }
                }.onFailure {
                    requireTokenInvalidRestart.value = true
                    sessionModule.invalidateSession()
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
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
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
                JacksonConverterFactory.create(
                    jacksonObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(kotlinModule())
                        .registerModule(JavaTimeModule())
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