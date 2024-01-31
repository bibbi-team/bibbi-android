package com.no5ing.bbibbi.presentation.feature.view.main.quit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.button.ToggleButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun QuitPageContent(
    currentSelection: SnapshotStateList<Int>,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.quit_heading_one),
            color = MaterialTheme.bbibbiScheme.icon,
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.quit_heading_two),
            color = MaterialTheme.bbibbiScheme.textPrimary,
            style = MaterialTheme.bbibbiTypo.headOne,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.quit_minimum_one),
            color = MaterialTheme.bbibbiScheme.icon,
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            quitReasons.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                        .clickable {
                            if (currentSelection.contains(index)) {
                                currentSelection.remove(index)
                            } else {
                                currentSelection.add(index)
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ToggleButton(
                        isToggled = currentSelection.contains(index),
                        onTap = {
                            if (currentSelection.contains(index)) {
                                currentSelection.remove(index)
                            } else {
                                currentSelection.add(index)
                            }
                        }
                    )
                    Text(
                        text = pair.second,
                        color = MaterialTheme.bbibbiScheme.textPrimary,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                }
            }
        }
    }
}