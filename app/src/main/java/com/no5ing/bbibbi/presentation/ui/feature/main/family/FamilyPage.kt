package com.no5ing.bbibbi.presentation.ui.feature.main.family

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMembersViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyPage(
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    onDispose: () -> Unit,
    onTapFamily: (Member) -> Unit,
    onTapShare: (String) -> Unit,
) {
    val members = familyMembersViewModel.uiState.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = members.loadState.refresh is LoadState.Loading,
        onRefresh = {
            members.refresh()
        }
    )
    LaunchedEffect(Unit) {
        familyMembersViewModel.invoke(Arguments())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.family_title)
            )
            Box(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 24.dp,
                )
            ) {
                FamilyPageInviteButton(
                    onTapShare = onTapShare
                )
            }
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = members.itemCount.toString(),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxWidth()
            ) {
                LazyColumn {
                    items(members.itemCount) {
                        val item = members[it] ?: throw RuntimeException()
                        MemberItem(
                            member = item,
                            isMe = familyMembersViewModel.me?.memberId == item.memberId,
                            modifier = Modifier.clickable {
                                onTapFamily(item)
                            },
                            onTap = {
                                onTapFamily(item)
                            }
                        )
                    }
                }
                PullRefreshIndicator(
                    members.loadState.refresh is LoadState.Loading,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }


        }
    }
}

@Composable
fun MemberItem(
    modifier: Modifier,
    member: Member,
    isMe: Boolean,
    onTap: () -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 14.dp, horizontal = 20.dp)
    ) {
        CircleProfileImage(
            member = member,
            size = 52.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.height(52.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = member.name,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
            )
            if (isMe)
                Text(
                    text = stringResource(id = R.string.family_me),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
        }
    }
}