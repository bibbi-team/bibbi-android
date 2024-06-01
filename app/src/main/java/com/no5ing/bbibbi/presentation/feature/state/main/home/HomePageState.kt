package com.no5ing.bbibbi.presentation.feature.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.model.post.PostType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime

@Stable
data class HomePageState(
    val uiPostState: StateFlow<Post>,
    val uiMemberState: StateFlow<Member>,
)

@Composable
fun rememberHomePageState(
    uiPostState: StateFlow<Post> = remember {
        MutableStateFlow(
            Post(
                postId = "",
                content = "",
                authorId = "",
                commentCount = 0,
                imageUrl = "https://picsum.photos/300/300?random=01",
                emojiCount = 0,
                createdAt = ZonedDateTime.now(),
                missionId = null,
                type = PostType.SURVIVAL,
            )
        )
    },
    uiMemberState: StateFlow<Member> = remember {
        MutableStateFlow(
            Member.unknown()
        )
    },
): HomePageState = HomePageState(
    uiPostState = uiPostState,
    uiMemberState = uiMemberState,
)