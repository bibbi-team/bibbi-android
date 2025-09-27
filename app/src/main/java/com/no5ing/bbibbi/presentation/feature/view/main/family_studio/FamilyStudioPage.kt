package com.no5ing.bbibbi.presentation.feature.view.main.family_studio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.AIPhotoInfoBaloon
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.component.button.CustomCTAButton
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.post.GetAIPostsViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.GetAiImageCountViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import java.time.LocalDate


@Composable
fun FamilyStudioPage(
    onDispose: () -> Unit = {},
    onTapCreateImage: () -> Unit = {},
    onTapAiPost: (AIPost) -> Unit = {},
    postsViewModel: GetAIPostsViewModel = hiltViewModel(),
    aiImageCountViewModel: GetAiImageCountViewModel = hiltViewModel(),
    isTermDialogEnabled: State<Boolean> = mutableStateOf(true),
    onDisagreeTerm: () -> Unit = {},
    onAgreeTerm: () -> Unit = {},
    onClickTerm: () -> Unit = {},
) {
    val aiImageState = aiImageCountViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        postsViewModel.invoke(Arguments())
        aiImageCountViewModel.invoke(Arguments())
    }
    val photoCount = if(aiImageState.value.isReady()) {
        aiImageState.value.data.familyAiImageCount
    } else {
        0
    }
    CustomAlertDialog(
        title = "이미지사용약관",
        description = "AI 이미지 기능을 사용하려면\n약관에 대한 동의가 필요해요",
        clickableTitle = "약관 전문 확인하기",
        enabledState = isTermDialogEnabled,
        confirmMessage = "동의하기",
        cancelMessage = "취소",
        confirmRequest = onAgreeTerm,
        cancelRequest = onDisagreeTerm,
        dismissRequest = onDisagreeTerm,
        onClickClickableTitle = onClickTerm,
    )
    BBiBBiSurface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                DisposableTopBar(
                    onDispose = onDispose,
                    title = "가족 사진관"
                )
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier
                                    .background(MaterialTheme.bbibbiScheme.icon, RoundedCornerShape(100.dp))
                                    .padding(vertical = 2.dp, horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = "추석",
                                        color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                        style = MaterialTheme.bbibbiTypo.bodyTwoBold,
                                    )
                                }
                                Box(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "9/29~10/27",
                                    color = MaterialTheme.bbibbiScheme.textPrimary,
                                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                AIPhotoInfoBaloon()
                            }
                            Text(
                                text = "${photoCount}개의 추억",
                                color = MaterialTheme.bbibbiScheme.textPrimary,
                                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                            )

                        }
                        Box(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.family_studio_banner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                }
                FamilyStudioPageFeed(
                    postItemsState = postsViewModel.uiState,
                    onTapContent = onTapAiPost,
                    onPullToRefresh = {
                        aiImageCountViewModel.invoke(Arguments())
                    }
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 15.dp)
                    .align(Alignment.BottomCenter)
            ) {
                CustomCTAButton(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    onClick = onTapCreateImage,
                    isActive = aiImageState.value.isReady() && aiImageState.value.data.hasAvailableImage()
                ) {
                    Text(
                        text = "이미지 만들기",
                        color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                        style = MaterialTheme.bbibbiTypo.bodyOneBold,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ai),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    if (aiImageState.value.isReady()) {
                        val count = aiImageState.value.data
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "(${count.availableAiImageCount}/3)",
                            color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                        )
                    }
                }
            }
        }



    }
}

@Preview(
    showBackground = true,
    name = "FamilyStudioPage",
    showSystemUi = true,
)
@Composable
fun FamilyStudioPAagePreview() {
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                DisposableTopBar(
                    onDispose = {},
                    title = "가족 사진관"
                )
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier
                                    .background(MaterialTheme.bbibbiScheme.icon, RoundedCornerShape(100.dp))
                                    .padding(vertical = 2.dp, horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = "추석",
                                        color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                        style = MaterialTheme.bbibbiTypo.bodyTwoBold,
                                    )
                                }
                                Box(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "10월 6일",
                                    color = MaterialTheme.bbibbiScheme.textPrimary,
                                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                                )
                            }
                            Text(
                                text = "[N개]의 추억",
                                color = MaterialTheme.bbibbiScheme.textPrimary,
                                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                            )

                        }
                        Box(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.family_studio_banner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 15.dp)
                    .align(Alignment.BottomCenter)
            ) {
                CustomCTAButton(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = "이미지 만들기",
                        color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                        style = MaterialTheme.bbibbiTypo.bodyOneBold,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ai),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }

    }
}