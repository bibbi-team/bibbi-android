package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMembersViewModel
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun HomePageStoryBar(
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(
        uiState = familyMembersViewModel.uiState
    ),
    items: LazyPagingItems<Member> = storyBarState.uiState.collectAsLazyPagingItems()
) {
    LaunchedEffect(Unit) {
        familyMembersViewModel.invoke(Arguments())
    }
    val meId = LocalSessionState.current.memberId
    val meState by retrieveMeViewModel.uiState.collectAsState()
    if (items.itemCount == 1) {
        HomePageNoFamilyBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp),
            onTap = onTapInvite,
        )
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            //horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
            }

            if (meState.isReady()) {
                val item = meState.data
                item {
                    StoryBarIcon(
                        member = item,
                        onTap = {
                            onTapProfile(item)
                        },
                        isMe = true
                    )
                }
            }


            items(items.itemCount) { index ->
                val item = items[index] ?: throw RuntimeException()
                if (item.memberId != meId) {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        StoryBarIcon(
                            member = item,
                            onTap = {
                                onTapProfile(item)
                            }
                        )
                    }
                }

            }

            item {
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }

}

@Composable
fun StoryBarIcon(
    onTap: () -> Unit,
    member: Member,
    isMe: Boolean = false,
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
            onTap = onTap,
        )
        Text(
            text = if(isMe) stringResource(id = R.string.family_me) else member.name,
            color = MaterialTheme.bbibbiScheme.textSecondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.bbibbiTypo.caption,
        )
    }
}