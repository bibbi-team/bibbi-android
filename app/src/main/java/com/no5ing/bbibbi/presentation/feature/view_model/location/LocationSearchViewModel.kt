package com.no5ing.bbibbi.presentation.feature.view_model.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.data.datasource.network.KakaoLocalApi
import com.no5ing.bbibbi.data.datasource.network.response.KakaoSearchDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val kakaoLocalApi: KakaoLocalApi,
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<KakaoSearchDocument>>(emptyList())
    val searchResults: StateFlow<List<KakaoSearchDocument>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun search(query: String, latitude: Double?, longitude: Double?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val response = kakaoLocalApi.searchKeyword(
                    authorization = "KakaoAK ${BuildConfig.kakaoRestApiKey}",
                    query = query,
                    x = longitude?.toString(),
                    y = latitude?.toString(),
                    radius = if (latitude != null && longitude != null) 20000 else null,
                    size = 15,
                )
                _searchResults.value = response.documents
            } catch (e: Exception) {
                Timber.e(e, "Kakao location search failed")
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
