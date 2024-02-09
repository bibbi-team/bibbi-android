package com.no5ing.bbibbi.presentation.feature.view.landing.already_family_exists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun AlreadyFamilyExistsView(
    onTapDispose: () -> Unit = {},
    onTapQuitAndJoin: () -> Unit = {},
) {
    val familyQuitModalEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_quit_group_title),
        description = stringResource(id = R.string.dialog_quit_group_message),
        enabledState = familyQuitModalEnabled,
        confirmMessage = stringResource(id = R.string.dialog_quit_group_confirm),
        confirmRequest = onTapQuitAndJoin
    )
    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    stringResource(id = R.string.already_family_exists_title),
                    color = MaterialTheme.bbibbiScheme.iconSelected,
                    style = MaterialTheme.bbibbiTypo.headOne,
                )
                Text(
                    stringResource(id = R.string.already_family_exists_subtitle),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
            }
            Image(
                painter = painterResource(R.drawable.sad_bbibbi),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .size(width = 209.dp, height = 243.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
            Column {
                CTAButton(
                    text = stringResource(id = R.string.already_family_exists_confirm),
                    onClick = onTapDispose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                )
                Spacer(Modifier.height(12.dp))
                CTAButton(
                    text = stringResource(id = R.string.already_family_exists_exit_family_and_join),
                    onClick = {
                        familyQuitModalEnabled.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "AlreadyFamilyExistsViewPreview",
    showSystemUi = true
)
@Composable
fun AlreadyFamilyExistsViewPreview() {
    BBiBBiPreviewSurface {
        AlreadyFamilyExistsView()
    }
}