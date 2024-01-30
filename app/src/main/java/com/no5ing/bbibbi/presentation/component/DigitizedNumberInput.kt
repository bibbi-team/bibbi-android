package com.no5ing.bbibbi.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun DigitizedNumberInput(
    baseDigit: Int = 4,
    digitName: String,
    value: Int,
    onValueChange: (String) -> Unit,
    isInvalidInput: Boolean,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { focusRequester.requestFocus() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = if (value != 0) value.toString() else "",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() },
            ),
            onValueChange = onValueChange,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.bbibbiTypo.title.copy(
                color = if (isInvalidInput)
                    MaterialTheme.bbibbiScheme.warningRed
                else
                    MaterialTheme.bbibbiScheme.textPrimary
            ),
            cursorBrush = Brush.verticalGradient(
                0.00f to MaterialTheme.bbibbiScheme.button,
                1.00f to MaterialTheme.bbibbiScheme.button,
            ),
            decorationBox = {
                Row {
                    Box {
                        it()
                        if (value == 0) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                Text(
                                    text = "0".repeat(baseDigit),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.bbibbiTypo.title,
                                    color = MaterialTheme.bbibbiScheme.button
                                )
                            }

                        }
                    }
                }

            }
        )
        Text(
            modifier = Modifier,
            text = digitName,
            style = MaterialTheme.bbibbiTypo.title,
            color = if (value == 0)
                MaterialTheme.bbibbiScheme.button
            else if (isInvalidInput)
                MaterialTheme.bbibbiScheme.warningRed
            else
                MaterialTheme.bbibbiScheme.textPrimary
        )
    }
}