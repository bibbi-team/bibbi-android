package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.BackToExitHandler
import com.no5ing.bbibbi.presentation.state.main.home.HomePageState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageState
import com.no5ing.bbibbi.presentation.ui.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.viewmodel.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.IsMeUploadedTodayViewModel

@Composable
fun HomePage(
    homePageState: HomePageState = rememberHomePageState(),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    isMeUploadedTodayViewModel: IsMeUploadedTodayViewModel = hiltViewModel(),
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
    onTapProfile: (Member) -> Unit = {},
    onTapContent: (Post) -> Unit = {},
    onTapUpload: () -> Unit = {},
    onTapInvite: () -> Unit = {},
) {
    val meUploadedState = isMeUploadedTodayViewModel.uiState.collectAsState()
    BackToExitHandler()
    LaunchedEffect(Unit) {
        isMeUploadedTodayViewModel.invoke(Arguments())
        retrieveMeViewModel.invoke(Arguments())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomePageTopBar(
                onTapLeft = onTapLeft,
                onTapRight = onTapRight
            )
            HomePageContent(
                onTapContent = onTapContent,
                onTapProfile = onTapProfile,
                onTapInvite = onTapInvite,
            )
        }
        if (meUploadedState.value.isReady() && !meUploadedState.value.data) {
            HomePageUploadButton(
                onTap = onTapUpload
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Preview",
    showSystemUi = true
)
@Composable
fun HomePagePreview() {
    BbibbiTheme {
        HomePage(
            retrieveMeViewModel = hiltViewModel(),
            isMeUploadedTodayViewModel = hiltViewModel(),
        )
    }
}

