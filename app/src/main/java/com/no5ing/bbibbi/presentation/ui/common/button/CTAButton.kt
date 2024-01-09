package com.no5ing.bbibbi.presentation.ui.common.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo

@Composable
fun CTAButton(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
    isActive: Boolean = true,
) {
    Button(
        shape = RoundedCornerShape(100.dp),
        onClick = { if (isActive) onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) MaterialTheme.bbibbiScheme.mainGreen else MaterialTheme.bbibbiScheme.mainGreen.copy(
                alpha = 0.2f
            )
        ),
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            style = MaterialTheme.bbibbiTypo.bodyOneBold,
        )
    }
}