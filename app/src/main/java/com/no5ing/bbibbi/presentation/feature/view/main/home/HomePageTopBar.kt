package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.familyNameTextStyle

@Composable
fun HomePageTopBar(
    isNewIconEnabled: Boolean,
    familyName: String?,
    onTapLeft: () -> Unit = {},
    onTapAlarm: () -> Unit = {},
    onTapRight: () -> Unit = {},
) {
    Box {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp)
        ) {
            if(familyName.isNullOrEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.bibbi_ci),
                    contentDescription = null,
                    modifier = Modifier
                        .height(18.dp)
                        .clickable {},
                    tint = Color(0xFFFFFFFF)
                )
            } else {
                Text(
                    text = familyName,
                    style = familyNameTextStyle,
                    color = MaterialTheme.bbibbiScheme.textPrimary,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clickable {
                        onTapLeft()
                    }
                    .padding(horizontal = 18.dp, vertical = 3.dp)

            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_people_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp),
                )
                if (isNewIconEnabled) {
                    Box(
                        modifier = Modifier.offset(x = 14.dp, y = (-3).dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.new_icon),
                            contentDescription = null,
                            modifier = Modifier.size(width = 29.dp, height = 14.dp)
                        )
                    }
                }

            }

            Row(
                modifier = Modifier.padding(end = 18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onTapAlarm() }
                        .padding(vertical = 3.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.alarm_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp),
                        tint = MaterialTheme.bbibbiScheme.icon
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clickable { onTapRight() }
                        .padding(vertical = 3.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp),
                        tint = MaterialTheme.bbibbiScheme.icon
                    )
                }
            }
        }
    }

}