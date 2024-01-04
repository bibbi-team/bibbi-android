package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMembersViewModel

@Composable
fun HomePageStoryBar(
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(
        uiState = familyMembersViewModel.uiState
    ),
) {
    LaunchedEffect(Unit) {
        familyMembersViewModel.invoke(Arguments())
    }
    val items = storyBarState.uiState.collectAsLazyPagingItems()
    if(items.itemCount == 1) {
        HomePageNoFamilyBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp),
            onTap = onTapInvite,
        )
    }else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }

            items(items.itemCount) { index ->
                val item = items[index] ?: throw RuntimeException()
                StoryBarIcon(
                    member = item,
                    onTap = {
                        onTapProfile(item)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

}

@Composable
fun StoryBarIcon(
    onTap: () -> Unit,
    member: Member,
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable { onTap() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleProfileImage(
            member = member,
            size = 64.dp,
        )
        Text(
            text = member.name,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}