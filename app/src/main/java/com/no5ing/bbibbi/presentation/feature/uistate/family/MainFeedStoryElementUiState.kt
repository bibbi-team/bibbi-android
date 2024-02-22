package com.no5ing.bbibbi.presentation.feature.uistate.family

import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member

data class MainFeedStoryElementUiState(
    val member: Member,
    val isUploadedToday: Boolean,
) : BaseModel()
