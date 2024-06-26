package com.no5ing.bbibbi.presentation.feature.state.register.day_of_birth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
data class RegisterDayOfBirthPageState(
    val yearTextState: MutableState<String>,
    val monthTextState: MutableState<String>,
    val dayTextState: MutableState<String>,
    val isInvalidYearState: MutableState<Boolean>,
    val isInvalidMonthState: MutableState<Boolean>,
    val isInvalidDayState: MutableState<Boolean>,
) {
    fun isInvalidInput() = isInvalidYearState.value ||
            isInvalidMonthState.value ||
            isInvalidDayState.value
}

@Composable
fun rememberRegisterDayOfBirthPageState(
    yearTextState: MutableState<String> = remember { mutableStateOf("") },
    monthTextState: MutableState<String> = remember { mutableStateOf("") },
    dayTextState: MutableState<String> = remember { mutableStateOf("") },
    isInvalidYearState: MutableState<Boolean> = remember { mutableStateOf(false) },
    isInvalidMonthState: MutableState<Boolean> = remember { mutableStateOf(false) },
    isInvalidDayState: MutableState<Boolean> = remember { mutableStateOf(false) }
): RegisterDayOfBirthPageState = remember {
    RegisterDayOfBirthPageState(
        yearTextState = yearTextState,
        monthTextState = monthTextState,
        dayTextState = dayTextState,
        isInvalidYearState = isInvalidYearState,
        isInvalidMonthState = isInvalidMonthState,
        isInvalidDayState = isInvalidDayState,
    )
}