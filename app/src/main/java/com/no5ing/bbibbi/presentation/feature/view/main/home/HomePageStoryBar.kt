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
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.feature.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.feature.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun HomePageStoryBar(
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(),
) {
    val postTopState by storyBarState.topState.collectAsState()
    val meId = LocalSessionState.current.memberId
    val meState by storyBarState.meState.collectAsState()
    val items = storyBarState.uiState.collectAsLazyPagingItems()

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
                        isMe = true,
                        isUploaded = postTopState.containsKey(item.memberId),
                        rank = postTopState[item.memberId] ?: -1,
                    )
                }
            }


            items(
                count = items.itemCount,
                key = { items[it]!!.memberId }
            ) { index ->
                val item = items[index] ?: throw RuntimeException()
                if (item.memberId != meId) {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        StoryBarIcon(
                            member = item,
                            onTap = {
                                onTapProfile(item)
                            },
                            isUploaded = postTopState.containsKey(item.memberId),
                            rank = postTopState[item.memberId] ?: -1,
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
            val rankColor = getRankColor(rank = rank)
            val rankBadge = getRankBadge(rank = rank)
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