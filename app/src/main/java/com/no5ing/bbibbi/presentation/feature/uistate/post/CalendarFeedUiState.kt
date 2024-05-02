package com.no5ing.bbibbi.presentation.feature.uistate.post

import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.DailyCalendarElement
import com.no5ing.bbibbi.data.model.post.Post

data class CalendarFeedUiState(
    val post: DailyCalendarElement,
    val writer: Member,
) : BaseModel()
