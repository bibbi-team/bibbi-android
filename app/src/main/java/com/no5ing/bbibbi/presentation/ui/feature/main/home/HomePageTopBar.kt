package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R

@Composable
fun HomePageTopBar(
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 3.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_people_icon),
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .clickable { onTapLeft() },
            tint = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            painter = painterResource(id = R.drawable.bibbi_ci),
            contentDescription = null,
            modifier = Modifier
                .height(18.dp)
                .clickable { onTapLeft() },
            tint = Color(0xffE8E8E8)
        )
        Icon(
            painter = painterResource(id = R.drawable.calendar_icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onTapRight() },
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}