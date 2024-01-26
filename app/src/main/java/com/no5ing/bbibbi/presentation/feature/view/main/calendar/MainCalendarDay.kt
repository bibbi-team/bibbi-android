package com.no5ing.bbibbi.presentation.feature.view.main.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import java.time.LocalDate


@Composable
fun <T : SelectionState> MainCalendarDay(
    modifier: Modifier = Modifier,
    state: DayState<T>,
    monthState: Map<LocalDate, CalendarElement>,
    onClick: (LocalDate) -> Unit = {},
) {
    val isSelectableState = state.selectionState is DynamicSelectionState
    val date = state.date
    val selectionState = state.selectionState
    val data = monthState[date]
    val isSelected = selectionState.isDateSelected(date)
    val textColor =
        (if (state.isCurrentDay && state.isFromCurrentMonth) MaterialTheme.bbibbiScheme.mainYellow
        else if (state.isFromCurrentMonth) MaterialTheme.bbibbiScheme.white
        else MaterialTheme.bbibbiScheme.button)
            .copy(
                alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
            )

    val boxBorderColor = if (isSelected) MaterialTheme.bbibbiScheme.white
    else if (state.isCurrentDay && state.isFromCurrentMonth) MaterialTheme.bbibbiScheme.mainYellow.copy(
        alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
    )
    else Color.Transparent

    val boxBgColor =
        (if (state.isFromCurrentMonth) MaterialTheme.bbibbiScheme.backgroundSecondary
        else MaterialTheme.bbibbiScheme.backgroundPrimary).copy(
            alpha = if (isSelectableState && !isSelected) 0.3f else 1.0f
        )

    val boxModifier = modifier
        //.size(50.dp)
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
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.isFromCurrentMonth) {
            Box {
                Box(
                    modifier = boxModifier,
                ) {
                    if (data?.representativeThumbnailUrl != null) {
                        AsyncImage(
                            model = asyncImagePainter(source = data.representativeThumbnailUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            alpha = if (isSelectableState && !isSelected) 0.3f else 0.5f
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = textColor,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                }
                if (data?.allFamilyMembersUploaded == true) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(horizontal = 3.dp, vertical = 3.dp)
                            .size(20.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fire_icon),
                            contentDescription = null,
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
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
            }
        }



        Spacer(modifier = Modifier.height(4.dp))
    }
}

