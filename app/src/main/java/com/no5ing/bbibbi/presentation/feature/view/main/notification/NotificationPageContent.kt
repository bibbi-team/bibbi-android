package com.no5ing.bbibbi.presentation.feature.view.main.notification

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.notification.NotificationModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.gapBetweenNow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationPageContent(
    notificationState: State<APIResponse<List<NotificationModel>>>,
    onRefresh: () -> Unit = {},
    onTapNotification: (NotificationModel) -> Unit = {},
) {
    val notifications by notificationState
    val pullRefreshState = rememberPullRefreshState(
        refreshing = notifications.isLoading(),
        onRefresh = onRefresh,
    )
    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxWidth()
    )  {
        LazyColumn{
            if (notifications.isReady()) {
                val notificationElements = notifications.data
                items(
                    count = notificationElements.size,
                    key = { notificationElements[it].notificationId }
                ) {
                    NotificationElement(
                        modifier = Modifier.clickable {
                             onTapNotification(notificationElements[it])
                        },
                        notificationModel = notificationElements[it]
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1.0f)
                            .background(MaterialTheme.bbibbiScheme.button)
                    )
                    Text(
                        "최근 한달 전 알림까지 확인할 수 있어요",
                        style = MaterialTheme.bbibbiTypo.caption,
                        color = MaterialTheme.bbibbiScheme.gray500,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1.0f)
                            .background(MaterialTheme.bbibbiScheme.button)
                    )
                }
            }
        }
        PullRefreshIndicator(
            notifications.isLoading(),
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
            contentColor = MaterialTheme.bbibbiScheme.iconSelected,
        )
    }
}

@Composable
fun NotificationElement(
    modifier: Modifier,
    notificationModel: NotificationModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            AsyncImage(
                model = asyncImagePainter(source = notificationModel.senderProfileImageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.bbibbiScheme.backgroundSecondary),
            )
            if (notificationModel.shouldDisplayAsBirthday) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.birthday_badge),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (5).dp, y = -(5).dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    notificationModel.title,
                    style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                    color = MaterialTheme.bbibbiScheme.white,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1.0f),
                )
                Text(
                    gapBetweenNow(time = notificationModel.createdAt),
                    color = MaterialTheme.bbibbiScheme.gray500,
                    style = MaterialTheme.bbibbiTypo.caption,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                notificationModel.content,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }



    }
}