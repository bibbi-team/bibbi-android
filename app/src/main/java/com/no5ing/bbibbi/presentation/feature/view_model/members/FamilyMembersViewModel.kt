package com.no5ing.bbibbi.presentation.feature.view_model.members

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.data.repository.member.GetMembersRepository
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FamilyMembersViewModel @Inject constructor(
    private val getMembersRepository: GetMembersRepository,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<PagingData<Member>>() {

    fun shouldShowFamilyNewIcon() = localDataStorage.getFamilyAndMemberNameFeatureFamily()
    fun hideShowFamilyNewIcon() {
        localDataStorage.setFamilyAndMemberNameFeatureFamily()
    }

    override fun initState(): PagingData<Member> {
        return PagingData.empty()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            getMembersRepository
                .fetch(arguments)
                .cachedIn(viewModelScope)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PagingData.empty()
                ).collectLatest {
                    setState(it)
                }
        }
    }

    override fun release() {
        super.release()
        getMembersRepository.closeResources()
    }
}