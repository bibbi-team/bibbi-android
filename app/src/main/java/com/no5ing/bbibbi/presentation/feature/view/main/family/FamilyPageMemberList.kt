package com.no5ing.bbibbi.presentation.feature.view.main.family

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyPageMemberList(
    meId: String,
    meState: State<APIResponse<Member>>,
    membersState: StateFlow<PagingData<Member>>,
    onTapProfile: (Member) -> Unit = {},
) {
    val members = membersState.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = members.loadState.refresh is LoadState.Loading,
        onRefresh = {
            members.refresh()
        }
    )
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.family_your_family),
                    style = MaterialTheme.bbibbiTypo.headOne.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.bbibbiScheme.textPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = members.itemCount.toString(),
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    color = MaterialTheme.bbibbiScheme.icon,
                )
            }
        }
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
        ) {
            LazyColumn {
                if (meState.value.isReady()) {
                    item {
                        val item = meState.value.data
                        FamilyPageMemberItem(
                            member = item,
                            isMe = true,
                            modifier = Modifier.clickable {
                                onTapProfile(item)
                            },
                            onTap = {
                                onTapProfile(item)
                            }
                        )
                    }
                }
                items(
                    count = members.itemCount,
                    key = { members[it]!!.memberId }
                ) {
                    val item = members[it] ?: throw RuntimeException()
                    if (item.memberId != meId) {
                        FamilyPageMemberItem(
                            member = item,
                            isMe = false,
                            modifier = Modifier.clickable {
                                onTapProfile(item)
                            },
                            onTap = {
                                onTapProfile(item)
                            }
                        )
                    }
                }
            }
            PullRefreshIndicator(
                members.loadState.refresh is LoadState.Loading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
                contentColor = MaterialTheme.bbibbiScheme.iconSelected,
            )
        }
    }
}