package com.no5ing.bbibbi.presentation.feature.view.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.toLocalizedDate

@Composable
fun ProfilePageMemberBar(
    viewerMemberId: String,
    memberState: State<APIResponse<Member>>,
    onTapChangeNickname: () -> Unit = {},
    onTapChangeProfileButton: () -> Unit = {},
    onTapProfileImage: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (memberState.value.isReady()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box {
                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    CircleProfileImage(
                        member = memberState.value.data,
                        size = 90.dp,
                        onTap = {
                            if (memberState.value.data.hasProfileImage()) {
                                onTapProfileImage(memberState.value.data.imageUrl!!)
                            }
                        }
                    )
                    if (memberState.value.data.isBirthdayToday) {
                        Image(
                            painter = painterResource(id = R.drawable.birthday_badge),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.TopEnd)
                                .offset((8).dp, (-8).dp)
                        )
                    }
                }
                if (viewerMemberId == memberState.value.data.memberId) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable {
                                onTapChangeProfileButton()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.change_image_icon),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = memberState.value.data.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.bbibbiScheme.textPrimary
                    )
                )
                if (viewerMemberId == memberState.value.data.memberId) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.write_icon),
                        contentDescription = null,
                        tint = MaterialTheme.bbibbiScheme.icon,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                onTapChangeNickname()
                            }
                    )
                }
            }
            if (viewerMemberId == memberState.value.data.memberId) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        id = R.string.register_at,
                        toLocalizedDate(memberState.value.data.familyJoinAt ?: "2000-01-01")
                    ),
                    style = MaterialTheme.bbibbiTypo.caption,
                    color = MaterialTheme.bbibbiScheme.icon,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.bbibbiScheme.backgroundSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}