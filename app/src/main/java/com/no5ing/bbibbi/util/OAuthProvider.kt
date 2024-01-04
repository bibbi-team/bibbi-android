package com.no5ing.bbibbi.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.no5ing.bbibbi.BuildConfig

fun googleSignInIntent(context: Context): Intent {
    val opt = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.googleAuthApiKey)
        .build()
    val signInClient = GoogleSignIn.getClient(context as Activity, opt)
    signInClient.signOut()
    return signInClient.signInIntent
}

fun kakaoSignIn(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailed: (Boolean) -> Unit,
) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onFailed(false)
            error.printStackTrace()
        } else if (token != null) {
            onSuccess(token.accessToken)
        } else {
            onFailed(false)
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onFailed(true)
                    return@loginWithKakaoTalk
                }
                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                onSuccess(token.accessToken)
            } else {
                onFailed(false) //일어날 수 있나?
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}