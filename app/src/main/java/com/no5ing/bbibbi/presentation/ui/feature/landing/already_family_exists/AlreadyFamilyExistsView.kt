package com.no5ing.bbibbi.presentation.ui.feature.landing.already_family_exists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton

@Composable
fun AlreadyFamilyExistsView(
    onTapDispose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
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
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                stringResource(id = R.string.already_family_exists_subtitle),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Image(
            painter = painterResource(R.drawable.ppippi),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(171.dp),
        )
        CTAButton(
            text = stringResource(id = R.string.already_family_exists_confirm),
            onClick = onTapDispose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 18.dp),
        )

    }
}