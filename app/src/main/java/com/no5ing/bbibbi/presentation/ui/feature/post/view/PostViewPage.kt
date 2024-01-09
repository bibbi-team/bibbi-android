package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.post.view.PostViewPageState
import com.no5ing.bbibbi.presentation.state.post.view.rememberPostViewPageState
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.FamilyPostViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.toLocalizedDate

@Composable
fun PostViewPage(
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    postId: String,
    familyPostViewModel: FamilyPostViewModel = hiltViewModel(),
    postViewPageState: PostViewPageState = rememberPostViewPageState(
        uiState = familyPostViewModel.uiState
    ),
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        familyPostViewModel.invoke(Arguments(resourceId = postId))
    }
    val postState by postViewPageState.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            postState.isReady(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = postState.data.post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(50.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.1f
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                if (postState.isReady()) {
                    PostViewTopBar(
                        onTap = {
                            onTapProfile(postState.data.writer)
                        },
                        onDispose = onDispose,
                        member = postState.data.writer,
                        date =
                        if (postState.isReady()) toLocalizedDate(postState.data.post.createdAt)
                        else ""
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    PostViewContent(
                        post = postState.data.post,
                        familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                        removePostReactionViewModel = removePostReactionViewModel,
                        addPostReactionViewModel = addPostReactionViewModel,
                    )
                }
            }
        }
    }


}

@Composable
fun PostViewTopBar(
    onTap: () -> Unit,
    onDispose: () -> Unit,
    date: String,
    member: Member,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.return_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(52.dp)
                .clickable { onDispose() }
        )
        CircleProfileImage(
            member = member,
            size = 40.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = member.name,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
            )
            Text(
                text = date,
                color = MaterialTheme.bbibbiScheme.icon,
                fontSize = 12.sp,
            )

        }
    }
}