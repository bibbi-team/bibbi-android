package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MemberRealEmojiListViewModel @Inject constructor(
    private val localDataStorage: LocalDataStorage,
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
) : BaseViewModel<Map<String, MemberRealEmoji>>() {
    init {
        loadDefault()
    }

    override fun initState(): Map<String, MemberRealEmoji> {
        return emptyMap()
    }

    private fun loadDefault() {
        setState(localDataStorage.getRealEmojiList().associateBy { it.type })
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            restAPI.getMemberApi().getRealEmojiList(
                memberId = sessionModule.sessionState.value.memberId,
            ).suspendOnSuccess {
                localDataStorage.setRealEmojiList(this.data.myRealEmojiList)
                setState(this.data.myRealEmojiList.associateBy { it.type })
            }
        }
    }

}