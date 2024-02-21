package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedStoryElementUiState
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomePageStoryBar(
    postTopStateFlow: StateFlow<APIResponse<List<MainFeedStoryElementUiState>>>,
    meStateFlow: StateFlow<APIResponse<Member>>,
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
) {
    val meId = LocalSessionState.current.memberId
    val postTopState by postTopStateFlow.collectAsState()
    val meState by meStateFlow.collectAsState()
    //val items = familyListStateFlow.collectAsLazyPagingItems()

    if (postTopState.isReady() && postTopState.data.size == 1) {
        HomePageNoFamilyBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp),
            onTap = onTapInvite,
        )
    } else if(postTopState.isReady()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
            }

            if (meState.isReady() && postTopState.isReady()) {
                val item = meState.data
                val meData = postTopState.data.indexOfFirst { it.member.memberId == meId }
                item {
                    StoryBarIcon(
                        member = item,
                        onTap = {
                            onTapProfile(item)
                        },
                        isMe = true,
                        isUploaded = postTopState.data[meData].isUploadedToday,
                        rank = meData,
                    )
                }
            }


            items(
                count = postTopState.data.size,
                key = { postTopState.data[it].member.memberId }
            ) { index ->
                val item = postTopState.data[index]
                if (item.member.memberId != meId) {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        StoryBarIcon(
                            member = item.member,
                            onTap = {
                                onTapProfile(item.member)
                            },
                            isUploaded = item.isUploadedToday,
                            rank = index,
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
    isUploaded: Boolean,
    rank: Int,
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable { onTap() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            val rankColor = if(isUploaded) getRankColor(rank = rank) else null
            val rankBadge = if(isUploaded) getRankBadge(rank = rank) else null
            if (rankColor != null) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(rankColor, CircleShape)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
            ) {
                CircleProfileImage(
                    member = member,
                    size = if (rankColor == null) 64.dp else 62.dp,
                    onTap = onTap,
                    opacity = if (isUploaded) 1.0f else 0.4f
                )
            }
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .size(64.dp)
            ) {
                if (member.isBirthdayToday) {
                    Image(
                        painter = painterResource(id = R.drawable.birthday_badge),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (4).dp, y = -(4).dp),
                    )
                }
            }
            if (rankBadge != null) {
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .size(64.dp)
                ) {
                    Image(
                        painter = painterResource(id = rankBadge),
                        contentDescription = null,
                        modifier = Modifier
                            .height(24.dp)
                            .offset(x = 0.dp, y = 2.dp),
                    )
                }
            }
        }
        Text(
            text = if (isMe) stringResource(id = R.string.family_me) else member.name,
            color = MaterialTheme.bbibbiScheme.textSecondary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.bbibbiTypo.caption,
        )
    }
}

@Composable
private fun getRankColor(rank: Int) = when (rank) {
    0 -> MaterialTheme.bbibbiScheme.mainYellow
    1 -> Color(0xff7FEC93)
    2 -> Color(0xffFFC98D)
    else -> null
}

private fun getRankBadge(rank: Int) = when (rank) {
    0 -> R.drawable.first_badge
    1 -> R.drawable.second_badge
    2 -> R.drawable.third_badge
    else -> null
}