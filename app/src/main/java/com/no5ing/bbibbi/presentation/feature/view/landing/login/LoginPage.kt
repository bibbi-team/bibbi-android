package com.no5ing.bbibbi.presentation.feature.view.landing.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BackToExitHandler
import com.no5ing.bbibbi.presentation.feature.state.landing.login.LoginPageState
import com.no5ing.bbibbi.presentation.feature.state.landing.login.LoginStatus
import com.no5ing.bbibbi.presentation.feature.state.landing.login.LoginSucceedResult
import com.no5ing.bbibbi.presentation.feature.state.landing.login.rememberLoginPageState
import com.no5ing.bbibbi.presentation.feature.view_model.auth.LoginWithCredentialsViewModel
import com.no5ing.bbibbi.util.googleSignInIntent
import com.no5ing.bbibbi.util.kakaoSignIn
import timber.log.Timber

@Composable
fun LoginPage(
    onCompleted: (LoginSucceedResult) -> Unit = {},
    loginViewModel: LoginWithCredentialsViewModel = hiltViewModel(),
    loginPageState: LoginPageState = rememberLoginPageState(
        uiState = loginViewModel.uiState.collectAsState(),
    )
) {
    val context = LocalContext.current
    val onKakaoSuccess: (String) -> Unit = {
        loginViewModel.invoke(
            Arguments(
                arguments = mapOf(
                    "authKey" to it,
                    "provider" to "kakao"
                )
            )
        )
    }
    val onKakaoFailed: (Boolean) -> Unit = { isCancelled ->
        loginPageState.isLoggingIn.value = false
        if (isCancelled) {
            Timber.d("[LoginPage] Kakao Login Cancelled by user")
        } else {
            Timber.e("[LoginPage] Kakao Login Failed")
        }
    }
    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val signInResult = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            signInResult.addOnCompleteListener {
                loginPageState.isLoggingIn.value = false
                try {
                    val authResult = it.getResult(ApiException::class.java)
                    val idToken = authResult.idToken ?: return@addOnCompleteListener
                    loginViewModel.invoke(
                        Arguments(
                            arguments = mapOf(
                                "authKey" to idToken,
                                "provider" to "google"
                            )
                        )
                    )
                } catch (e: ApiException) {
                    Timber.e("[LoginPage] Google Login Failed", e)
                }
            }
        }


    BackToExitHandler()
    LaunchedEffect(loginPageState.uiState.value) {
        Timber.d("[LoginPage] uiState = ${loginPageState.uiState.value}")
        when (loginPageState.uiState.value) {
            LoginStatus.REJECTED -> {
                loginPageState.isLoggingIn.value = false
            }

            LoginStatus.SUCCEED_PERMANENT_HAS_FAMILY -> {
                loginPageState.isLoggingIn.value = false
                loginViewModel.resetState()
                onCompleted(LoginSucceedResult.PERMANENT_HAS_FAMILY)
            }

            LoginStatus.SUCCEED_PERMANENT_NO_FAMILY -> {
                loginPageState.isLoggingIn.value = false
                loginViewModel.resetState()
                onCompleted(LoginSucceedResult.PERMANENT_NO_FAMILY)
            }

            LoginStatus.SUCCEED_TEMPORARY -> {
                loginPageState.isLoggingIn.value = false
                loginViewModel.resetState()
                onCompleted(LoginSucceedResult.TEMPORARY)
            }

            else -> {}
        }
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
            .systemBarsPadding()
    ) {
        LoginPageContent(
            isLoggingIn = loginPageState.isLoggingIn.value,
            onTapKakao = {
                loginPageState.isLoggingIn.value = true
                kakaoSignIn(
                    context = context,
                    onSuccess = onKakaoSuccess,
                    onFailed = onKakaoFailed
                )
            },
            onTapGoogle = {
                loginPageState.isLoggingIn.value = true
                startForResult.launch(googleSignInIntent(context))
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "LoginPagePreview",
    showSystemUi = true
)
@Composable
fun LoginPagePreview() {
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 10.dp)
                    .systemBarsPadding(),
            ) {
                LoginPageContent(
                    isLoggingIn = false,
                )
            }
        }
    }
}