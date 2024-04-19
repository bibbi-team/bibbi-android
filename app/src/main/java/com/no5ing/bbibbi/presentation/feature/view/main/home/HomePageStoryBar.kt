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
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.view.MainPageModel
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedStoryElementUiState
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomePageStoryBar(
    mainPageState: StateFlow<APIResponse<MainPageModel>>,
    deferredPickStateSet: StateFlow<Set<String>>,
    onTapProfile: (String) -> Unit = {},
    onTapPick: (MainPageTopBarModel) -> Unit = {},
    onTapInvite: () -> Unit = {},
) {
    val meId = LocalSessionState.current.memberId
    val mainPageModel by mainPageState.collectAsState()
    val deferredPickSet = deferredPickStateSet.collectAsState()
    val items = if (mainPageModel.isReady()) mainPageModel.data.topBarElements else emptyList()

    if (items.size == 1) {
        HomePageNoFamilyBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp),
            onTap = onTapInvite,
        )
    } else if (items.size > 1) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }

            items(
                count = items.size,
                key = { items[it].memberId }
            ) { index ->
                val item = items[index]
                Row {
                    Spacer(modifier = Modifier.width(12.dp))
                    StoryBarIcon(
                        member = item,
                        onTap = {
                            onTapProfile(item.memberId)
                        },
                        isUploaded = item.displayRank != null,
                        rank = index,
                        isMe = item.memberId == meId,
                        isInDeferredPickState = deferredPickSet.value.contains(item.memberId),
                        onTapPick = {
                            onTapPick(item)
                        }
                    )
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
    onTapPick: () -> Unit,
    member: MainPageTopBarModel,
    isInDeferredPickState: Boolean = false,
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
            val rankColor = if (isUploaded) getRankColor(rank = rank) else null
            val rankBadge = if (isUploaded) getRankBadge(rank = rank) else null
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
                    noImageLetter = member.noImageLetter,
                    imageUrl = member.imageUrl,
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
                if (member.shouldShowBirthdayMark) {
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
            if (member.shouldShowPickIcon && !isInDeferredPickState) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .size(64.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pick_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .height(32.dp)
                            .clickable { onTapPick() }
                            .offset(x = 6.dp, y = 4.dp),
                    )
                }
            }
        }
        Text(
            text = if (isMe) stringResource(id = R.string.family_me) else member.displayName,
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