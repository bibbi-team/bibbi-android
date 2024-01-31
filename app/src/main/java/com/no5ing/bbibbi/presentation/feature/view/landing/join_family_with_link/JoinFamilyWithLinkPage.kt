package com.no5ing.bbibbi.presentation.feature.view.landing.join_family_with_link


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.state.landing.join_family_with_link.JoinFamilyWithLinkPageState
import com.no5ing.bbibbi.presentation.feature.state.landing.join_family_with_link.rememberJoinFamilyWithLinkPageState
import com.no5ing.bbibbi.presentation.feature.view_model.family.JoinFamilyWithLinkViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalDeepLinkState
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.getLinkIdFromUrl
import com.no5ing.bbibbi.util.localResources

@Composable
fun JoinFamilyWithLinkPage(
    joinFamilyWithLinkViewModel: JoinFamilyWithLinkViewModel = hiltViewModel(),
    state: JoinFamilyWithLinkPageState = rememberJoinFamilyWithLinkPageState(),
    onDispose: () -> Unit,
    onJoinComplete: () -> Unit,
) {
    val mixPanel = LocalMixpanelProvider.current
    val uiState by joinFamilyWithLinkViewModel.uiState.collectAsState()
    val textBoxFocus = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val snackBarHost = LocalSnackbarHostState.current
    val deepLinkState = LocalDeepLinkState.current
    val resources = localResources()
    LaunchedEffect(uiState) {
        when {
            uiState.isReady() -> onJoinComplete()
            uiState.isFailed() -> {
                val errMessage = resources.getErrorMessage(uiState.errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errMessage,
                    actionLabel = snackBarWarning,
                )
                joinFamilyWithLinkViewModel.resetState()
            }
        }
    }
    LaunchedEffect(Unit) {
        textBoxFocus.requestFocus()
    }
    LaunchedEffect(deepLinkState) {
        if (deepLinkState != null) {
            if (isValidUrl(deepLinkState)) {
                val linkSuffix = getLinkIdFromUrl(deepLinkState)
                if (linkSuffix.length == 8) {
                    state.nicknameTextState.value = linkSuffix
                    state.isInvalidInputState.value = false
                    state.ctaButtonEnabledState.value = true
                }
            }
        }
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DisposableTopBar(
                title = "",
                onDispose = onDispose
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.join_family_with_link_title),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = state.nicknameTextState.value,
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                    onValueChange = {
                        state.nicknameTextState.value = it
                        if (it.length != 8) {
                            if (isValidUrl(it)) {
                                val linkSuffix = getLinkIdFromUrl(it)
                                if (linkSuffix.length == 8) {
                                    state.nicknameTextState.value = linkSuffix
                                    state.isInvalidInputState.value = false
                                    state.ctaButtonEnabledState.value = true
                                    return@BasicTextField
                                }
                            }
                            state.isInvalidInputState.value = true
                            state.ctaButtonEnabledState.value = false
                        } else {
                            state.isInvalidInputState.value = false
                            state.ctaButtonEnabledState.value = true
                        }
                    },
                    singleLine = true,
                    decorationBox = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box {
                                it()
                                if (state.nicknameTextState.value.isEmpty()) {
                                    Box(modifier = Modifier.align(Alignment.Center)) {
                                        Text(
                                            text = stringResource(id = R.string.join_family_with_link_sample_text),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.bbibbiTypo.title,
                                        )
                                    }

                                }
                            }
                        }


                    },
                    cursorBrush = Brush.verticalGradient(
                        0.00f to MaterialTheme.bbibbiScheme.button,
                        1.00f to MaterialTheme.bbibbiScheme.button,
                    ),
                    textStyle = MaterialTheme.bbibbiTypo.title.copy(
                        textAlign = TextAlign.Center,
                        color = if (state.isInvalidInputState.value)
                            MaterialTheme.bbibbiScheme.warningRed
                        else
                            MaterialTheme.bbibbiScheme.textPrimary
                    ),
                    modifier = Modifier
                        .focusRequester(textBoxFocus),
                )
                if (state.isInvalidInputState.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.warning_circle_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp),
                            tint = MaterialTheme.bbibbiScheme.warningRed,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.join_family_with_link_not_valid),
                            color = MaterialTheme.bbibbiScheme.warningRed,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular
                        )
                    }

                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                CTAButton(
                    text = stringResource(id = R.string.join_family_with_link_enter_group),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    isActive = state.ctaButtonEnabledState.value && uiState.isIdle(),
                    onClick = {
                        mixPanel.track("Click_Enter_Group_2")
                        joinFamilyWithLinkViewModel.invoke(
                            Arguments(
                                arguments = mapOf(
                                    "linkId" to state.nicknameTextState.value,
                                )
                            )
                        )
                    },
                )
            }

        }
    }
}

private fun isValidUrl(input: String): Boolean {
    val linkPrefix = "https://no5ing.kr/o/"
    return input.startsWith(linkPrefix)
}