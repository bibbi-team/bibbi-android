package com.no5ing.bbibbi

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.AddFcmTokenRequest
import com.no5ing.bbibbi.di.NetworkModule
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.feature.MainPage
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.navigate
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.navigateUnsafeDeepLink
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.AlreadyFamilyExistsPageController
import com.no5ing.bbibbi.presentation.feature.view_controller.landing.AlreadyFamilyExistsPageController.goAlreadyFamilyExistsPage
import com.no5ing.bbibbi.presentation.navigation.NavDestinationListener
import com.no5ing.bbibbi.presentation.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.LocalDeepLinkState
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.MixpanelWrapper
import com.no5ing.bbibbi.util.forceRestart
import com.no5ing.bbibbi.util.getInstallReferrerClient
import com.no5ing.bbibbi.util.getLinkIdFromUrl
import com.no5ing.bbibbi.widget.WIDGET_DEEPLINK_KEY
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val deepLinkStateFlow = MutableStateFlow<String?>(null)
    private val pendingDeepLinkDestination = MutableStateFlow<String?>(null)

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        //HANDLE FOREGROUND INTENT (already app is active)
        intent = newIntent
        Timber.d("onNewIntent: $newIntent")
        onAppStartIntent(newIntent)
    }

    private fun onAppStartIntent(newIntent: Intent?) {
        Timber.d("onAppStartIntent: $newIntent")
        val appLinkData: Uri? = newIntent?.data
        val deepLink = newIntent?.extras?.getString("aosDeepLink")
        Timber.d("DeepLink: $deepLink")
        val linkId = appLinkData?.let {
            deepLinkStateFlow.value = it.toString()
            getLinkIdFromUrl(it.toString())
        }
        if (linkId != null) {
            handleDeepLinkId(linkId)
        }
        deepLink?.let {
            pendingDeepLinkDestination.value = it
            return
        }

        val widgetExtraData = newIntent?.extras?.getString(WIDGET_DEEPLINK_KEY) ?: return
        pendingDeepLinkDestination.value = widgetExtraData
    }

    private fun handleDeepLinkId(linkId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            restAPI.getLinkApi().getLink(linkId).suspendOnSuccess {
                val deepLinkPayload = data
                when (deepLinkPayload.type) {
                    "FAMILY_REGISTRATION" -> {
                        val sessionState = sessionModule.sessionState.value
                        if (!sessionState.isLoggedIn() || !sessionState.hasFamily()) {
                            //localDataStorage.setRegistrationToken(linkId)
                        } else {
                            //님머함?
                            runOnUiThread {
                                localNavController?.goAlreadyFamilyExistsPage(linkId)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepSplash by mutableStateOf(true)
        // var isAlreadyLoggedIn by mutableStateOf(false)
        var isReady by mutableStateOf(false)
        var isInitialBootstrap by mutableStateOf(true)

        lifecycleScope.launch(Dispatchers.IO) {
            handleFirstInstall()
            initializeSession()
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
            val navController = rememberNavController()
            val snackBarHostState = remember { SnackbarHostState() }
            val sessionState by sessionModule.sessionState.collectAsState()
            val deepLinkState by deepLinkStateFlow.collectAsState()
            val pendingDeepLinkState by pendingDeepLinkDestination.collectAsState()
            val mixPanelState = remember {
                MixpanelWrapper().apply {
                    mixpanelAPI = MixpanelAPI.getInstance(
                        this@MainActivity,
                        BuildConfig.mixPanelToken,
                        true
                    )
                }
            }
            DisposableEffect(navController) {
                localNavController = navController
                navController
                    .addOnDestinationChangedListener(NavDestinationListener(this@MainActivity))
                onDispose {
                    localNavController = null
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
                    CompositionLocalProvider(LocalMixpanelProvider provides mixPanelState) {

                        CompositionLocalProvider(value = LocalDeepLinkState provides deepLinkState) {
                            BbibbiTheme {
                                CustomAlertDialog(
                                    enabledState = updateState,
                                    title = stringResource(id = R.string.app_update_dialog_title),
                                    description = stringResource(id = R.string.app_update_dialog_message),
                                    confirmMessage = stringResource(id = R.string.app_update_dialog_positive),
                                    confirmRequest = this::openMarketAndShutdown,
                                    dismissRequest = this::openMarketAndShutdown,
                                    cancelRequest = this::openMarketAndShutdown,
                                    hasCancel = false,
                                )
                                CustomAlertDialog(
                                    enabledState = tokenInvalidState,
                                    title = stringResource(id = R.string.token_invalid_dialog_title),
                                    description = stringResource(id = R.string.token_invalid_dialog_message),
                                    confirmMessage = stringResource(id = R.string.token_invalid_dialog_positive),
                                    confirmRequest = this::resetAuthenticationState,
                                    dismissRequest = this::resetAuthenticationState,
                                    cancelRequest = this::resetAuthenticationState,
                                    hasCancel = false,
                                )
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.bbibbiScheme.backgroundPrimary)
                                        .statusBarsPadding(),
                                    color = MaterialTheme.bbibbiScheme.backgroundPrimary
                                ) {
                                    AnimatedVisibility(
                                        visible = isReady,
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        LaunchedEffect(sessionState) {
                                            Timber.d("[MainActivity] Session State Changed to $sessionState")
                                            if (sessionState.isLoggedIn()) {
                                                mixPanelState.mixpanelAPI.identify(sessionState.memberId)
                                                mixPanelState.mixpanelAPI.registerSuperPropertiesMap(
                                                    mapOf(
                                                        "memberId" to sessionState.memberId,
                                                        "appKey" to BuildConfig.appKey,
                                                    )
                                                )
                                                localDataStorage.setAuthTokens(sessionState.apiToken)
                                            } else {
                                                mixPanelState.mixpanelAPI.reset()
                                                localDataStorage.logOut()
                                            }
                                        }
                                        CompositionLocalProvider(
                                            LocalSessionState provides sessionState
                                        ) {
                                            val isAlreadyLoggedIn = sessionState.isLoggedIn()
                                                    && sessionState.hasFamily()
                                            LaunchedEffect(
                                                isAlreadyLoggedIn,
                                                pendingDeepLinkState
                                            ) {
                                                if (isAlreadyLoggedIn && pendingDeepLinkState != null) {
                                                    val deepLinkUrl = pendingDeepLinkState!!
                                                    pendingDeepLinkDestination.value = null
                                                    navController.navigateUnsafeDeepLink(
                                                        deepLinkUrl
                                                    )
                                                }
                                            }
                                            MainPage(
                                                snackBarHostState = snackBarHostState,
                                                navController = navController,
                                                isAlreadyLoggedIn = isAlreadyLoggedIn,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetAuthenticationState() {
        NetworkModule.requireTokenInvalidRestart.value = false
        forceRestart()
    }

    private fun openMarketAndShutdown() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=com.no5ing.bbibbi")
        this.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}