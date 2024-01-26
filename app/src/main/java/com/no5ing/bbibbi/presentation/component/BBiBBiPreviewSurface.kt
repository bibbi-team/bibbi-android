package com.no5ing.bbibbi.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.feature.uistate.common.SessionState
import com.no5ing.bbibbi.presentation.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.MixpanelWrapper

@Composable
fun BBiBBiPreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BbibbiTheme {
        Surface(
            color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            modifier = modifier,
        ) {
            CompositionLocalProvider(LocalMixpanelProvider provides MixpanelWrapper() ) {

                CompositionLocalProvider(
                    LocalSessionState provides SessionState(
                        isLoggedIn = true,
                        _memberId = Member.unknown().memberId,
                    )
                ) {
                    content()
                }
            }
        }
    }

}