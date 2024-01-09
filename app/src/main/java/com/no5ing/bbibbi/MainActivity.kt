package com.no5ing.bbibbi

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.AddFcmTokenRequest
import com.no5ing.bbibbi.di.NetworkModule
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.ui.MainPage
import com.no5ing.bbibbi.presentation.ui.navigation.NavDestinationListener
import com.no5ing.bbibbi.presentation.ui.navigation.destination.LandingAlreadyFamilyExistsDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.navigate
import com.no5ing.bbibbi.presentation.ui.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.forceRestart
import com.no5ing.bbibbi.util.getInstallReferrerClient
import com.no5ing.bbibbi.util.getLinkIdFromUrl
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var localDataStorage: LocalDataStorage

    @Inject
    lateinit var sessionModule: SessionModule

    @Inject
    lateinit var restAPI: RestAPI

    private var localNavController: NavHostController? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //HANDLE FOREGROUND INTENT (already app is active)
        Timber.d("onNewIntent: $intent")
        onAppStartIntent(intent)
    }

    private fun onAppStartIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        Timber.d("onAppStartIntent: $intent")
        val appLinkData: Uri? = intent?.data
        val linkId = appLinkData?.let {
            getLinkIdFromUrl(it.toString())
        }
        if (linkId != null) {
            handleDeepLinkId(linkId)
        }
    }

    private fun handleDeepLinkId(linkId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            restAPI.getLinkApi().getLink(linkId).suspendOnSuccess {
                val deepLinkPayload = data
                when (deepLinkPayload.type) {
                    "FAMILY_REGISTRATION" -> {
                        val sessionState = sessionModule.sessionState.value
                        if (!sessionState.isLoggedIn() || !sessionState.hasFamily()) {
                            localDataStorage.setRegistrationToken(linkId)
                        } else {
                            //님머함?
                            runOnUiThread {
                                localNavController?.navigate(LandingAlreadyFamilyExistsDestination)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun initializeDefault() {
        handleFirstInstall()
        initializeSession()
    }

    private suspend fun initializeSession() {
        if (localDataStorage.getAuthTokens() != null) {
            val fcm = FirebaseMessaging.getInstance().token.await()
            sessionModule.onLoginWithTemporaryCredentials(
                newTokenPair = localDataStorage.getAuthTokens()!!,
            )
            restAPI.getMemberApi().getMeInfo().suspendOnSuccess {
                val isValidUser = data.hasFamily()
                if (isValidUser) {
                    Timber.d("[Auth] Register FCM Token: $fcm")
                    restAPI.getMemberApi().registerFcmToken(
                        AddFcmTokenRequest(
                            fcmToken = fcm
                        )
                    )
                }
                sessionModule.onLoginWithCredentials(
                    newTokenPair = localDataStorage.getAuthTokens()!!,
                    member = data,
                )
            }.suspendOnFailure {
                sessionModule.invalidateSession()
            }
        }
    }

    private suspend fun handleFirstInstall() {
        if (!localDataStorage.getLandingSeen()) {
            localDataStorage.setLandingSeen()
            Timber.d("Initial installation! get referrer")
            try {
                getInstallReferrerClient(this)?.apply {
                    val response = installReferrer.installReferrer
                    response.split("&").forEach {
                        val keyVal = it.split("=")
                        if (keyVal.size == 2) {
                            if (keyVal[0] == "referrer") {
                                handleDeepLinkId(keyVal[1])
                            }
                        }
                    }
                    Timber.d("Install referrer: $response")
                }
            } catch (_: Exception) {
            }
        }
    }


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepSplash by mutableStateOf(true)
        // var isAlreadyLoggedIn by mutableStateOf(false)
        var isReady by mutableStateOf(false)
        var isInitialBootstrap by mutableStateOf(true)

        lifecycleScope.launch(Dispatchers.IO) {
            initializeDefault()
            keepSplash = false
            isReady = true
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplash
            }
        }

        setContent {
            val updateState = NetworkModule.requireUpdateState.collectAsState()
            val tokenInvalidState = NetworkModule.requireTokenInvalidRestart.collectAsState()
            val navController = rememberAnimatedNavController()
            val snackBarHostState = remember { SnackbarHostState() }
            val sessionState by sessionModule.sessionState.collectAsState()
            DisposableEffect(navController) {
                localNavController = navController
                navController
                    .addOnDestinationChangedListener(NavDestinationListener(this@MainActivity))
                onDispose {
                    localNavController = null
                }
            }

            LaunchedEffect(updateState.value) {
                if (updateState.value) {
                    openRequireUpdateDialog()
                }
            }

            LaunchedEffect(tokenInvalidState.value) {
                if (tokenInvalidState.value) {
                    NetworkModule.requireTokenInvalidRestart.value = false
                    openRequireLoginDialog()
                }
            }

            LaunchedEffect(isInitialBootstrap, isReady) {
                if (isInitialBootstrap && isReady) {
                    isInitialBootstrap = false
                    onAppStartIntent(intent)
                }
            }
            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState
            ) {
                CompositionLocalProvider(
                    LocalNavigateControllerState provides navController
                ) {
                    BbibbiTheme {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.bbibbiScheme.backgroundPrimary)
                                .statusBarsPadding(),
                            color = MaterialTheme.bbibbiScheme.backgroundPrimary
                        ) {
                            AnimatedVisibility(visible = isReady) {
                                LaunchedEffect(sessionState) {
                                    Timber.d("[MainActivity] Session State Changed to $sessionState")
                                    if (sessionState.isLoggedIn()) {
                                        localDataStorage.setAuthTokens(sessionState.apiToken)
                                    } else {
                                        localDataStorage.logOut()
                                    }
                                }
                                CompositionLocalProvider(
                                    LocalSessionState provides sessionState
                                ) {
                                    MainPage(
                                        snackBarHostState = snackBarHostState,
                                        navController = navController,
                                        isAlreadyLoggedIn = sessionState.isLoggedIn()
                                                && sessionState.hasFamily(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openRequireUpdateDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(this.getString(R.string.app_update_dialog_title))
            .setMessage(this.getString(R.string.app_update_dialog_message))
            .setPositiveButton(this.getString(R.string.app_update_dialog_positive)) { _, _ ->
                openMarketAndShutdown()
            }
            .setOnCancelListener {
                openMarketAndShutdown()
            }
            .create()
            .show()
    }

    private fun openRequireLoginDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(this.getString(R.string.token_invalid_dialog_title))
            .setMessage(this.getString(R.string.token_invalid_dialog_message))
            .setPositiveButton(this.getString(R.string.token_invalid_dialog_positive)) { _, _ ->
                forceRestart()
            }
            .setOnCancelListener {
                forceRestart()
            }
            .create()
            .show()
    }

    private fun openMarketAndShutdown() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=com.no5ing.bbibbi")
        this.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}