package com.no5ing.bbibbi.presentation.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
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
            )
        )
    },
    uiMemberState: StateFlow<Member> = remember {
        MutableStateFlow(
            Member(
                memberId = "",
                name = "하나밖에 없는 혈육",
                imageUrl = "https://picsum.photos/300/300?random=01",
                familyId = null,
                dayOfBirth = "2021-01-01",
            )
        )
    },
): HomePageState = HomePageState(
    uiPostState = uiPostState,
    uiMemberState = uiMemberState,
)