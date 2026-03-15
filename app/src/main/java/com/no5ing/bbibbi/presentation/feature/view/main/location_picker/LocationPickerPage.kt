package com.no5ing.bbibbi.presentation.feature.view.main.location_picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.feature.view_model.location.LocationSearchViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun LocationPickerPage(
    onDispose: () -> Unit = {},
    onConfirmLocation: (latitude: Double, longitude: Double, address: String) -> Unit = { _, _, _ -> },
    currentLatitude: Double? = null,
    currentLongitude: Double? = null,
    locationSearchViewModel: LocationSearchViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }
    val searchResults by locationSearchViewModel.searchResults.collectAsState()
    val isLoading by locationSearchViewModel.isLoading.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (currentLatitude != null && currentLongitude != null) {
            locationSearchViewModel.search("", currentLatitude, currentLongitude)
        }
    }

    BBiBBiSurface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.location_picker_title),
            )

            // Search bar
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.location_search_hint),
                        color = MaterialTheme.bbibbiScheme.icon,
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = null,
                        tint = MaterialTheme.bbibbiScheme.icon,
                        modifier = Modifier.size(20.dp),
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        locationSearchViewModel.search(query, currentLatitude, currentLongitude)
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.bbibbiScheme.backgroundHover,
                    unfocusedContainerColor = MaterialTheme.bbibbiScheme.backgroundHover,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.bbibbiScheme.textPrimary,
                    unfocusedTextColor = MaterialTheme.bbibbiScheme.textPrimary,
                    cursorColor = MaterialTheme.bbibbiScheme.mainYellow,
                ),
            )

            // Results
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.bbibbiScheme.mainYellow,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            } else if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "아직 위치가 없어요.\n검색으로 위치를 추가할 수 있어요.",
                        color = MaterialTheme.bbibbiScheme.gray500,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(searchResults) { document ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val lat = document.y.toDoubleOrNull() ?: return@clickable
                                    val lng = document.x.toDoubleOrNull() ?: return@clickable
                                    val address = document.placeName
                                    onConfirmLocation(lat, lng, address)
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                        ) {
                            Text(
                                text = document.placeName,
                                color = MaterialTheme.bbibbiScheme.textPrimary,
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val distanceText = when {
                                document.distance.isNotEmpty() -> formatDistance(document.distance)
                                currentLatitude != null && currentLongitude != null -> {
                                    val docLat = document.y.toDoubleOrNull()
                                    val docLng = document.x.toDoubleOrNull()
                                    if (docLat != null && docLng != null) {
                                        formatDistance(calculateDistance(currentLatitude, currentLongitude, docLat, docLng).toString())
                                    } else null
                                }
                                else -> null
                            }
                            Text(
                                text = distanceText ?: document.roadAddressName.ifEmpty { document.addressName },
                                color = MaterialTheme.bbibbiScheme.textSecondary,
                                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
    val r = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return (r * c).toInt()
}

private fun formatDistance(distanceStr: String): String {
    val meters = distanceStr.toIntOrNull() ?: return distanceStr
    return if (meters >= 1000) {
        String.format("%.1fkm", meters / 1000.0)
    } else {
        "${meters}m"
    }
}
