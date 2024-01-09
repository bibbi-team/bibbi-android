package com.no5ing.bbibbi.presentation.ui.feature.landing.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.landing.login.LoginPageState
import com.no5ing.bbibbi.presentation.state.landing.login.LoginStatus
import com.no5ing.bbibbi.presentation.state.landing.login.LoginSucceedResult
import com.no5ing.bbibbi.presentation.state.landing.login.rememberLoginPageState
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.util.BackToExitHandler
import com.no5ing.bbibbi.presentation.viewmodel.auth.LoginWithCredentialsViewModel
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

            }

            LoginStatus.SUCCEED_PERMANENT_HAS_FAMILY -> {
                onCompleted(LoginSucceedResult.PERMANENT_HAS_FAMILY)
            }

            LoginStatus.SUCCEED_PERMANENT_NO_FAMILY -> {
                onCompleted(LoginSucceedResult.PERMANENT_NO_FAMILY)
            }

            LoginStatus.SUCCEED_TEMPORARY -> {
                onCompleted(LoginSucceedResult.TEMPORARY)
            }

            else -> {}
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.login_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(225.dp)
                        .height(115.dp),
                    tint = MaterialTheme.bbibbiScheme.white
                )
                Image(
                    painter = painterResource(id = R.drawable.login_backgroup),
                    contentDescription = null,
                    modifier = Modifier
                        .width(275.dp)
                        .height(207.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                KakaoLoginButton(
                    onClick = {
                        kakaoSignIn(
                            context = context,
                            onSuccess = onKakaoSuccess,
                            onFailed = onKakaoFailed
                        )
                        //  loginViewModel.invoke(Arguments())
                    }
                )
                GoogleLoginButton(
                    onClick = {
                        startForResult.launch(googleSignInIntent(context))
                    }
                )
            }

        }
    }
}

@Composable
fun KakaoLoginButton(
    onClick: () -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.bbibbiScheme.kakaoYellow),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.kakao_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(19.dp),
                tint = Color.Black
            )
            Text(
                text = stringResource(id = R.string.login_with_kakao),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            )
        }

    }
}

@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.bbibbiScheme.white),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.google.firebase.messaging.R.drawable.googleg_standard_color_18),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp),
            )
            Text(
                text = stringResource(id = R.string.login_with_google),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            )
        }

    }
}