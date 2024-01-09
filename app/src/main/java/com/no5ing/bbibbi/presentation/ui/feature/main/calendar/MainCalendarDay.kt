package com.no5ing.bbibbi.presentation.ui.feature.main.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.presentation.ui.theme.mainGreen
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import java.time.LocalDate


@Composable
fun <T : SelectionState> MainCalendarDay(
    state: DayState<T>,
    monthState: Map<LocalDate, CalendarElement>,
    onClick: (LocalDate) -> Unit = {},
) {
    val isSelectableState = state.selectionState is DynamicSelectionState
    val date = state.date
    val selectionState = state.selectionState
    val data = monthState[date]
    val isSelected = selectionState.isDateSelected(date)
    val textColor = (if (state.isCurrentDay && state.isFromCurrentMonth) mainGreen
    else if (state.isFromCurrentMonth) Color.White
    else MaterialTheme.colorScheme.surface)
        .copy(
            alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
        )

    val boxBorderColor = if (isSelected) Color.White
    else if (state.isCurrentDay && state.isFromCurrentMonth) mainGreen.copy(
        alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
    )
    else Color.Transparent

    val boxBgColor =
        (if (state.isFromCurrentMonth) MaterialTheme.colorScheme.onBackground
        else MaterialTheme.colorScheme.background).copy(
            alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
        )

    val boxModifier = Modifier
        .size(50.dp)
        .clip(RoundedCornerShape(13.dp))
        .border(
            width = 1.dp,
            boxBorderColor,
            RoundedCornerShape(13.dp)
        )
        .background(
            color = boxBgColor,
        )



    Column(
        modifier = Modifier.clickable {
            onClick(date)
        }
    ) {
        if (state.isFromCurrentMonth) {
            Box {
                Box(
                    modifier = boxModifier,
                ) {
                    AsyncImage(
                        model = data?.representativeThumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = if (isSelectableState && !isSelected) 0.3f else 0.5f
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (data?.allFamilyMembersUploaded == true) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            //   .offset(x = (-4).dp, y = 4.dp)
                            .size(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bbibbi_smile),
                            contentDescription = null,
                            tint = mainGreen,
                            modifier = Modifier
                                .fillMaxSize(),
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = boxModifier,
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }



        Spacer(modifier = Modifier.height(4.dp))
    }
}

