package com.no5ing.bbibbi.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.AddFcmTokenRequest
import com.no5ing.bbibbi.di.NetworkModule
import com.no5ing.bbibbi.presentation.ui.navigation.destination.CameraViewDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.LandingAlreadyFamilyExistsDestination
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.cameraViewRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.composable
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.dialog
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.landingPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.mainPageRoute
import com.no5ing.bbibbi.presentation.ui.navigation.destination.NavigationDestination.Companion.navigate
import com.no5ing.bbibbi.presentation.ui.navigation.graph.landingGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.mainGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.postGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.registerGraph
import com.no5ing.bbibbi.presentation.ui.navigation.graph.settingGraph
import com.no5ing.bbibbi.presentation.ui.theme.BbibbiTheme
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.forceRestart
import com.no5ing.bbibbi.util.getLinkIdFromUrl
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var localDataStorage: LocalDataStorage

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
        Timber.d("onAppStartIntent: $intent")
        val appLinkData: Uri? = intent?.data
        val linkId = appLinkData?.let {
            getLinkIdFromUrl(it.toString())
        }
        if(linkId != null) {
            handleDeepLinkId(linkId)
        }
    }

    private fun handleDeepLinkId(linkId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            restAPI.getLinkApi().getLink(linkId).suspendOnSuccess {
                val deepLinkPayload = data
                when(deepLinkPayload.type) {
                    "FAMILY_REGISTRATION" -> {
                        val previousMe = localDataStorage.getMe()
                        if (previousMe == null || !previousMe.hasFamily()) {
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
            }.suspendOnFailure {

            }
        }
    }

    private suspend fun initializeDefault(): Boolean {
        var landingSkippable = false
        //TODO: CHECK VERSION FIRST
        if (localDataStorage.getAuthTokens() != null) {
            val fcm = FirebaseMessaging.getInstance().token.await()
            restAPI.getMemberApi().getMeInfo().suspendOnSuccess {
                localDataStorage.setMe(data)

                val isValidUser = data.hasFamily()
                if (isValidUser) {
                    restAPI.getMemberApi().registerFcmToken(
                        AddFcmTokenRequest(
                            fcmToken = fcm
                        )
                    )
                }
                landingSkippable = isValidUser
            }.suspendOnFailure {
                //TODO: SOME operation?
                localDataStorage.logOut()
            }
        }
        return landingSkippable
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
        onAppStartIntent(appLinkIntent)

        Timber.d("appLinkAction: $appLinkAction")
        Timber.d("appLinkData: $appLinkData")

        val keepSplash = mutableStateOf(true)
        val landingSkippable = mutableStateOf(false)
        var isReady by mutableStateOf(false)
        var isInitialBootstrap by mutableStateOf(true)

        lifecycleScope.launch(Dispatchers.IO) {
            landingSkippable.value = initializeDefault()
            keepSplash.value = false
            isReady = true
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Timber.d("FirebaseMessaging token: $it")
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplash.value
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            val updateState = NetworkModule.requireUpdateState.collectAsState()
            val tokenInvalidState = NetworkModule.requireTokenInvalidRestart.collectAsState()
            val startDestination = if (landingSkippable.value)
                mainPageRoute
            else
                landingPageRoute
            val navController = rememberAnimatedNavController()
            DisposableEffect(navController) {
                localNavController = navController
                onDispose {
                    localNavController = null
                }
            }

            val hostState = remember {
                SnackbarHostState()
            }
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                val routes = controller.currentBackStack.value.joinToString("->") {
                    it.destination.route ?: "START"
                }
                Timber.e(routes + "->${destination.route ?: "END"}")
            }

            LaunchedEffect(updateState.value) {
                if (updateState.value) {
                    openRequireUpdateDialog()
                }
            }

            LaunchedEffect(tokenInvalidState.value) {
                if (tokenInvalidState.value) {
                    openRequireLoginDialog()
                }
            }

            LaunchedEffect(isInitialBootstrap, isReady) {
                if (isInitialBootstrap && isReady) {
                    isInitialBootstrap = false
                    onAppStartIntent(appLinkIntent)
                }
            }

            BbibbiTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides hostState
                    ) {
                        CompositionLocalProvider(
                            LocalNavigateControllerState provides navController
                        ) {
                            AnimatedVisibility(visible = isReady) {
                                Scaffold(
                                    snackbarHost = {
                                        CustomSnackBarHost(
                                            hostState = hostState
                                        )
                                    }
                                ) { innerPadding ->
                                    Box(
                                        Modifier.padding(
                                            start = innerPadding.calculateStartPadding(
                                                LayoutDirection.Ltr
                                            ),
                                            top = 0.dp,
                                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                                        )
                                    ) {
                                        AnimatedNavHost(
                                            navController = navController,
                                            startDestination = startDestination,
                                            enterTransition = { fadeIn(animationSpec = tween(250)) },
                                            exitTransition = { fadeOut(animationSpec = tween(200)) },
                                        ) {
                                            landingGraph(
                                                navController = navController,
                                            )
                                            registerGraph(
                                                navController = navController,
                                            )
                                            mainGraph(
                                                navController = navController,
                                            )
                                            settingGraph(
                                                navController = navController,
                                            )
                                            postGraph(
                                                navController = navController,
                                            )
                                            composable(
                                                controller = navController,
                                                destination = CameraViewDestination,
                                                enterTransition = {
                                                    slideInVertically {
                                                        it
                                                    }
                                                },
                                                popExitTransition = {
                                                    slideOutVertically {
                                                        it
                                                    }
                                                }
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