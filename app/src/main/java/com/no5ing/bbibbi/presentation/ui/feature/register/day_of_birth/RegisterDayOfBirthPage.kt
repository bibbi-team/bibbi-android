package com.no5ing.bbibbi.presentation.ui.feature.register.day_of_birth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.state.register.day_of_birth.RegisterDayOfBirthPageState
import com.no5ing.bbibbi.presentation.state.register.day_of_birth.rememberRegisterDayOfBirthPageState
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo

@Composable
fun RegisterDayOfBirthPage(
    nickName: String,
    onNextPage: (String) -> Unit,
    state: RegisterDayOfBirthPageState = rememberRegisterDayOfBirthPageState()
) {
    val yearFocus = remember { FocusRequester() }
    val monthFocus = remember { FocusRequester() }
    val dayFocus = remember { FocusRequester() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.register_day_of_birth_description,
                        nickName
                    ),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DigitizedNumberInput(
                        baseDigit = 4,
                        digitName = stringResource(id = R.string.register_day_of_birth_year),
                        value = state.yearTextState.value,
                        focusRequester = yearFocus,
                        onValueChange = {
                            val number = it.toIntOrNull() ?: return@DigitizedNumberInput
                            if (number < 0 || number > 9999) return@DigitizedNumberInput
                            state.isInvalidYearState.value = number > 2023 || number < 1900 //TODO
                            if (it.length == 4 && state.yearTextState.value / 100 > 0) monthFocus.requestFocus()
                            state.yearTextState.value = number
                        },
                        isInvalidInput = state.isInvalidInput()
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DigitizedNumberInput(
                        baseDigit = 1,
                        digitName = stringResource(id = R.string.register_day_of_birth_month),
                        value = state.monthTextState.value,
                        focusRequester = monthFocus,
                        onValueChange = {
                            val number = it.toIntOrNull() ?: return@DigitizedNumberInput
                            if (number < 0 || number > 99) return@DigitizedNumberInput
                            state.isInvalidMonthState.value = number > 12
                            if (it.length == 2 && state.monthTextState.value < 10) dayFocus.requestFocus()
                            state.monthTextState.value = number
                        },
                        isInvalidInput = state.isInvalidInput()
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DigitizedNumberInput(
                        baseDigit = 1,
                        digitName = stringResource(id = R.string.register_day_of_birth_day),
                        value = state.dayTextState.value,
                        focusRequester = dayFocus,
                        onValueChange = {
                            val number = it.toIntOrNull() ?: return@DigitizedNumberInput
                            if (number < 0 || number > 99) return@DigitizedNumberInput
                            state.isInvalidDayState.value = number > 31
                            state.dayTextState.value = number
                        },
                        isInvalidInput = state.isInvalidInput()
                    )
                }
                if (state.isInvalidInput()) {
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
                            text = stringResource(id = R.string.register_dob_correct_date),
                            color = MaterialTheme.bbibbiScheme.warningRed,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_dob_description),
                    color = MaterialTheme.bbibbiScheme.icon,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
                CTAButton(
                    text = stringResource(id = R.string.register_continue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    onClick = {
                        val yearStr = String.format("%04d", state.yearTextState.value)
                        val monthStr = String.format("%02d", state.monthTextState.value)
                        val dayStr = String.format("%02d", state.dayTextState.value)
                        onNextPage(
                            "${yearStr}-${monthStr}-${dayStr}"
                        )
                    },
                    isActive = !state.isInvalidInput() && state.dayTextState.value != 0
                            && state.monthTextState.value != 0 && state.yearTextState.value != 0,
                )
            }

        }
    }
    LaunchedEffect(Unit) {
        yearFocus.requestFocus()
    }
}

@Composable
fun DigitizedNumberInput(
    baseDigit: Int = 4,
    digitName: String,
    value: Int,
    onValueChange: (String) -> Unit,
    isInvalidInput: Boolean,
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = if (value != 0) value.toString() else "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            modifier = Modifier.clickable { focusRequester.requestFocus() },
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