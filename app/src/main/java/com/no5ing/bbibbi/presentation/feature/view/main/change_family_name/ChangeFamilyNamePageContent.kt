package com.no5ing.bbibbi.presentation.feature.view.main.change_family_name

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChangeFamilyNamePageContent(
    textBoxFocus: FocusRequester,
    onSubmit: (String) -> Unit = {},
    isProcessing: Boolean,
    lastChangeMemberState: StateFlow<APIResponse<Member>>,
    nicknameTextState: MutableState<String>,
    invalidInputDescState: MutableState<String>,
    isInvalidInputState: MutableState<Boolean>,
) {
    val maxWord = 10
    val wordExceedMessage = stringResource(id = R.string.register_nickname_word_below_n, maxWord)
    val focusHost = LocalFocusManager.current
    val lastChangeMember by lastChangeMemberState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.0f)
        ) {
            Text(
                text = stringResource(id = R.string.change_family_name),
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.headTwoBold,
            )
            BasicTextField(
                value = nicknameTextState.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    val prevWord = nicknameTextState.value
                    if (it.length > maxWord) {
                        isInvalidInputState.value = true
                        invalidInputDescState.value = wordExceedMessage
                    } else if (prevWord != it) {
                        nicknameTextState.value = it
                        isInvalidInputState.value = false
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
                            if (nicknameTextState.value.isEmpty()) {
                                Box(modifier = Modifier.align(Alignment.Center)) {
                                    Text(
                                        text = stringResource(id = R.string.change_family_name_sample_text),
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
                    color = if (isInvalidInputState.value)
                        MaterialTheme.bbibbiScheme.warningRed
                    else
                        MaterialTheme.bbibbiScheme.textPrimary
                ),
                modifier = Modifier.focusRequester(textBoxFocus),
            )
            if (isInvalidInputState.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.warning_circle_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = MaterialTheme.bbibbiScheme.warningRed
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = invalidInputDescState.value,
                        color = MaterialTheme.bbibbiScheme.warningRed,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                }

            }

        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(lastChangeMember.isReady()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "마지막 수정 :",
                        color = MaterialTheme.bbibbiScheme.icon,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MiniCircledIcon(
                        noImageLetter = lastChangeMember.data.name.first().toString(),
                        imageUrl = lastChangeMember.data.imageUrl,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lastChangeMember.data.name,
                        color = MaterialTheme.bbibbiScheme.icon,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                }
            }
            CTAButton(
                text = stringResource(id = R.string.change_nickname_complete),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(vertical = 18.dp),
                isActive = nicknameTextState.value.length in 2..maxWord && !isProcessing,
                onClick = {
                    focusHost.clearFocus()
                    onSubmit(nicknameTextState.value)
                },
            )
        }

    }
}

@Composable
private fun MiniCircledIcon(
    modifier: Modifier = Modifier,
    noImageLetter: String,
    imageUrl: String?,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.bbibbiScheme
                                .backgroundHover,
                            CircleShape
                        )
                )
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = noImageLetter,
                        fontSize = 10.sp,
                        color = MaterialTheme.bbibbiScheme.white,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
