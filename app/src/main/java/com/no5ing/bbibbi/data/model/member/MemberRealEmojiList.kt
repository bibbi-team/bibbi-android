package com.no5ing.bbibbi.data.model.member

import com.no5ing.bbibbi.data.model.BaseModel

data class MemberRealEmojiList<T : BaseModel>(
    val myRealEmojiList: List<T>
)
