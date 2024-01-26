package com.no5ing.bbibbi.presentation.feature.view.main.family

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun FamilyPageMemberItem(
    modifier: Modifier,
    member: Member,
    isMe: Boolean,
    onTap: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Box(
                contentAlignment = Alignment.TopEnd,
            ) {
                CircleProfileImage(
                    member = member,
                    size = 52.dp,
                    onTap = onTap,
                )
                if (member.isBirthdayToday) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.birthday_badge),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (5).dp, y = -(5).dp),
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.height(52.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    color = MaterialTheme.bbibbiScheme.textPrimary,
                )
                if (isMe)
                    Text(
                        text = stringResource(id = R.string.family_me),
                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                        color = MaterialTheme.bbibbiScheme.icon,
                    )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.arrow_right_bold),
            contentDescription = null,
            tint = MaterialTheme.bbibbiScheme.icon,
            modifier = Modifier.size(width = 7.dp, height = 12.dp),
        )

    }
}