package com.no5ing.bbibbi.presentation.feature.view.landing.already_family_exists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.AlreadyFamilyExistsViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun AlreadyFamilyExistsView(
    linkId: String,
    onTapDispose: () -> Unit = {},
    onTapQuitAndJoin: () -> Unit = {},
    alreadyFamilyExistsViewModel: AlreadyFamilyExistsViewModel = hiltViewModel(),
) {
    val familyQuitModalEnabled = remember { mutableStateOf(false) }
    val uiState by alreadyFamilyExistsViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        alreadyFamilyExistsViewModel.invoke(
            arguments = Arguments(arguments = mapOf("linkId" to linkId))
        )
    }
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility (uiState.isReady()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        val chunkedList = uiState.data.familyMembersProfileImageUrls.subList(0, 2)
                        val dataOffset = if(uiState.data.extraFamilyMembersCount > 0) 1 else 0
                        val totalMove = (chunkedList.size - 1) + dataOffset
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = 24.dp * totalMove * -1),
                            contentAlignment = Alignment.Center,
                        ) {

                            chunkedList.forEachIndexed { index, imageUrl ->
                                val actualName = uiState.data.familyMemberNames.getOrNull(index)
                                FamilyIcon(
                                    noImageLetter = actualName?.first()?.toString() ?: "?",
                                    imageUrl = imageUrl,
                                    modifier = Modifier.offset(
                                        x = 48.dp * index
                                    )
                                )
                            }
                            if(uiState.data.extraFamilyMembersCount > 0) {
                                MoreFamilyIcon(
                                    remainCnt = uiState.data.extraFamilyMembersCount,
                                    modifier = Modifier.offset(
                                        x = 48.dp * chunkedList.size
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "${uiState.data.familyMembersCount}명의 구성원",
                            color = MaterialTheme.bbibbiScheme.textSecondary,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
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

@Composable
fun FamilyIcon(
    modifier: Modifier = Modifier,
    noImageLetter: String,
    imageUrl: String?,
    onTap: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.bbibbiScheme.backgroundPrimary, CircleShape)
        ) {

        }
        if (imageUrl != null) {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .clickable { onTap() },
            )
        } else {
            Box(
                modifier = Modifier.clickable { onTap() },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
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
                        fontSize = 30.sp,
                        color = MaterialTheme.bbibbiScheme.white,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun MoreFamilyIcon(
    modifier: Modifier = Modifier,
    remainCnt: Int,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.bbibbiScheme.backgroundPrimary, CircleShape)
        ) {

        }
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.bbibbiScheme
                            .backgroundHover,
                        CircleShape
                    )
            )
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "+${remainCnt}",
                    fontSize = 30.sp,
                    color = MaterialTheme.bbibbiScheme.white,
                    fontWeight = FontWeight.SemiBold,
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
        AlreadyFamilyExistsView(linkId = "")
    }
}