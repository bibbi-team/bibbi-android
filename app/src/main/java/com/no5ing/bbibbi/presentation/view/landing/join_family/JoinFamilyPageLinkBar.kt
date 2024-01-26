package com.no5ing.bbibbi.presentation.view.landing.join_family

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalMixpanelProvider

@Composable
fun JoinFamilyPageLinkBar(
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mixPanel = LocalMixpanelProvider.current
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.bbibbiScheme.backgroundSecondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                vertical = 22.dp,
                horizontal = 20.dp,
            )
            .clickable {
                mixPanel.track("Click_Enter_Group_1 ")
                onTap()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.invite_icon),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(13.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.join_family_link_bar_title),
                        color = MaterialTheme.bbibbiScheme.textSecondary,
                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.join_family_link_bar_subtitle),
                        color = MaterialTheme.bbibbiScheme.textPrimary,
                        style = MaterialTheme.bbibbiTypo.headTwoBold,
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = null,
                tint = MaterialTheme.bbibbiScheme.icon,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}