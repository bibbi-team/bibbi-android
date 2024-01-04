package com.no5ing.bbibbi.presentation.uistate.family

import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post

data class MainFeedUiState(
    val post: Post,
    val writer: Member,
) : BaseModel()
