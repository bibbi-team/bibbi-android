package com.no5ing.bbibbi.data.datasource.network.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSearchResponse(
    val meta: KakaoSearchMeta,
    val documents: List<KakaoSearchDocument>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSearchMeta(
    @JsonProperty("total_count") val totalCount: Int,
    @JsonProperty("pageable_count") val pageableCount: Int,
    @JsonProperty("is_end") val isEnd: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoSearchDocument(
    val id: String = "",
    @JsonProperty("place_name") val placeName: String = "",
    @JsonProperty("address_name") val addressName: String = "",
    @JsonProperty("road_address_name") val roadAddressName: String = "",
    val x: String = "",
    val y: String = "",
    val phone: String = "",
    val distance: String = "",
    @JsonProperty("category_name") val categoryName: String = "",
)
