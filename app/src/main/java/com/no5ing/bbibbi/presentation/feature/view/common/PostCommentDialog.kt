package com.no5ing.bbibbi.presentation.feature.view.common

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.component.DraggableCardComplex
import com.no5ing.bbibbi.presentation.component.ModalBottomSheet
import com.no5ing.bbibbi.presentation.component.rememberModalBottomSheetState
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.uistate.post.PostCommentUiState
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination.Companion.navigate
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController
import com.no5ing.bbibbi.presentation.feature.view_model.post.CreatePostCommentViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.DeletePostCommentViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.PostCommentViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalNavigateControllerState
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.gapBetweenNow
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.pxToDp
import timber.log.Timber

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PostCommentDialog(
    postId: String,
    isEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    textBoxFocus: FocusRequester = remember { FocusRequester() },
    commentViewModel: PostCommentViewModel = hiltViewModel(),
    createPostCommentViewModel: CreatePostCommentViewModel = hiltViewModel(),
    deletePostCommentViewModel: DeletePostCommentViewModel = hiltViewModel(),
) {
    if (isEnabled.value) {
        val snackBarHost = LocalSnackbarHostState.current
        val focusManager = LocalFocusManager.current
        val navController = LocalNavigateControllerState.current
        val memberId = LocalSessionState.current.memberId
        val keyboardText = remember {
            mutableStateOf("")
        }
        val listState = rememberLazyListState()
        val cardRevealState = remember { mutableStateMapOf<String, Unit>() }
        var pauseNextScroll by remember {
            mutableStateOf(false)
        }

        val uiState = commentViewModel.uiState.collectAsLazyPagingItems()
        LaunchedEffect(Unit) {
            if (commentViewModel.isInitialize()) {
                commentViewModel.invoke(
                    Arguments(
                        arguments = mapOf(
                            "postId" to postId
                        )
                    )
                )
            } else {
                commentViewModel.refresh()
            }
        }

        LaunchedEffect(uiState.loadState.refresh) {
            if (pauseNextScroll) {
                pauseNextScroll = false
                return@LaunchedEffect
            }
            if (uiState.loadState.refresh is LoadState.NotLoading && uiState.itemCount > 0) {
                listState.animateScrollToItem(uiState.itemCount - 1)
            }
        }

        val resources = localResources()
        val createCommentUiState by createPostCommentViewModel.uiState.collectAsState()
        LaunchedEffect(createCommentUiState.status) {
            when (createCommentUiState.status) {
                is APIResponse.Status.SUCCESS -> {
                    cardRevealState.clear()
                    createPostCommentViewModel.resetState()
                    uiState.refresh()
                }

                is APIResponse.Status.ERROR -> {
                    cardRevealState.clear()
                    createPostCommentViewModel.resetState()
                    snackBarHost.showSnackBarWithDismiss(
                        message = resources.getErrorMessage(createCommentUiState.errorCode),
                        actionLabel = snackBarWarning
                    )
                }

                else -> {}
            }
        }

        val deleteCommentUiState by deletePostCommentViewModel.uiState.collectAsState()
        LaunchedEffect(deleteCommentUiState.status) {
            when (deleteCommentUiState.status) {
                is APIResponse.Status.SUCCESS -> {
                    cardRevealState.clear()
                    deletePostCommentViewModel.resetState()
                    pauseNextScroll = true
                    uiState.refresh()
                    snackBarHost.showSnackBarWithDismiss(
                        message = resources.getString(R.string.comment_dialog_comment_deleted),
                        actionLabel = snackBarWarning
                    )
                }

                is APIResponse.Status.ERROR -> {
                    cardRevealState.clear()
                    deletePostCommentViewModel.resetState()
                    snackBarHost.showSnackBarWithDismiss(
                        message = resources.getErrorMessage(createCommentUiState.errorCode),
                        actionLabel = snackBarWarning
                    )
                }

                else -> {}
            }
        }

        Box(
            modifier = Modifier
        ) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { isEnabled.value = false },
                windowInsets = WindowInsets.ime
                    .union(WindowInsets.navigationBars)
                    .union(WindowInsets.statusBars),
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                containerColor = MaterialTheme.bbibbiScheme.backgroundPrimary,
                dragHandle = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            Modifier
                                .size(width = 36.dp, height = 5.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(MaterialTheme.bbibbiScheme.button)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = stringResource(id = R.string.comment_dialog_title),
                            color = MaterialTheme.bbibbiScheme.textPrimary,
                            style = MaterialTheme.bbibbiTypo.bodyOneBold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.bbibbiScheme.gray600
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f),
                        state = listState,
                    ) {
                        if (uiState.itemCount == 0) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Spacer(modifier = Modifier.height(60.dp))
                                    Text(
                                        text = stringResource(id = R.string.comment_dialog_no_comments_title),
                                        color = Color.White,
                                        style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = stringResource(id = R.string.comment_dialog_no_comments_description),
                                        color = MaterialTheme.bbibbiScheme.gray500,
                                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                                    )
                                }
                            }
                        } else {
                            items(
                                count = uiState.itemCount,
                                key = { index ->
                                    uiState[index]!!.commentId
                                }
                            ) {
                                val item = uiState[it] ?: throw RuntimeException()
                                CommentBox(
                                    modifier = Modifier
                                        .animateItemPlacement(
                                            animationSpec = tween(durationMillis = 350)
                                        ),
                                    comment = item,
                                    onTapProfile = { member ->
                                        navController.navigate(
                                            destination = ProfilePageController,
                                            path = member.memberId
                                        )
                                    },
                                    onTapDelete = {
                                        deletePostCommentViewModel.invoke(
                                            Arguments(
                                                arguments = mapOf(
                                                    "postId" to postId,
                                                    "commentId" to item.commentId
                                                )
                                            )
                                        )
                                    },
                                    isMyComment = item.memberId == memberId,
                                    isRevealed = cardRevealState.containsKey(item.commentId),
                                    setRevealState = {
                                        if (it) {
                                            cardRevealState[item.commentId] = Unit
                                        } else {
                                            cardRevealState.remove(item.commentId)
                                        }
                                    }
                                )
                            }
                        }

                    }
                    KeyboardBar(
                        focusRequester = textBoxFocus,
                        keyboardText = keyboardText,
                        onSend = {
                            if (keyboardText.value.isNotEmpty()) {
                                createPostCommentViewModel.invoke(
                                    Arguments(
                                        arguments = mapOf(
                                            "postId" to postId,
                                            "content" to keyboardText.value
                                        )
                                    )
                                )
                                keyboardText.value = ""
                                focusManager.clearFocus()
                            }
                        }
                    )

                }

            }
        }


    }
}

@Composable
fun KeyboardBar(
    keyboardText: MutableState<String>,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onSend: () -> Unit,
) {
    var keyboardTextStr by keyboardText
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.bbibbiScheme.backgroundSecondary)
            .padding(vertical = 12.dp, horizontal = 16.dp)
        // verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
            // .border(1.dp, MaterialTheme.bbibbiScheme.gray600, RoundedCornerShape(20.dp))

        ) {
            BasicTextField(
                value = keyboardTextStr,
                modifier = Modifier
                    .weight(1.0f)
                    .focusRequester(focusRequester),
                onValueChange = { nextValue ->
                    keyboardText.value = nextValue
                },
                decorationBox = {
                    if (keyboardText.value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.comment_dialog_text_field_hint),
                            color = MaterialTheme.bbibbiScheme.gray500,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular
                        )
                    } else {
                        it()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                ),
                textStyle = MaterialTheme.bbibbiTypo.bodyOneRegular.copy(
                    color = MaterialTheme.bbibbiScheme.textPrimary
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        Timber.d("Done")
                    }
                ),
                cursorBrush = Brush.verticalGradient(
                    0.00f to MaterialTheme.bbibbiScheme.button,
                    1.00f to MaterialTheme.bbibbiScheme.button,
                ),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (keyboardTextStr.isNotBlank()) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                keyboardTextStr = ""
                            },
                        tint = MaterialTheme.bbibbiScheme.icon
                    )
                }

                Text(
                    text = stringResource(id = R.string.comment_dialog_text_field_enter),
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    color = if (keyboardTextStr.isEmpty()) MaterialTheme.bbibbiScheme.gray500
                    else MaterialTheme.bbibbiScheme.mainYellow,
                    modifier = Modifier.clickable {
                        onSend()
                    }
                )
            }

        }

    }
}

@Composable
fun CommentBox(
    modifier: Modifier,
    comment: PostCommentUiState,
    onTapProfile: (Member) -> Unit,
    onTapDelete: () -> Unit,
    isMyComment: Boolean,
    isRevealed: Boolean,
    setRevealState: (Boolean) -> Unit
) {
    val areaWidthPx = remember { 200.0f }
    var relevantHeight by remember { mutableFloatStateOf(0.0f) }
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(areaWidthPx.pxToDp())
                    .background(MaterialTheme.bbibbiScheme.warningRed)
                    .height(relevantHeight.pxToDp())
                    .clickable { onTapDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.comment_dialog_delete),
                    color = Color.White,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular
                )
            }

        }
        DraggableCardComplex(
            isRevealed = isRevealed,
            cardOffset = areaWidthPx,
            backgroundColor = MaterialTheme.bbibbiScheme.backgroundPrimary,
            onGloballyPositioned = {
                relevantHeight = it.boundsInWindow().height
            },
            isRevealable = isMyComment,
            onExpand = { setRevealState(true) },
            onCollapse = { setRevealState(false) }) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircleProfileImage(
                    size = 44.dp,
                    member = comment.member ?: Member.unknown(),
                    onTap = {
                        if (comment.member != null) onTapProfile(comment.member)
                    }
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = comment.member?.name ?: "unknown",
                            color = MaterialTheme.bbibbiScheme.iconSelected,
                            style = MaterialTheme.bbibbiTypo.bodyTwoBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = gapBetweenNow(time = comment.createdAt),
                            color = MaterialTheme.bbibbiScheme.gray500,
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular
                        )
                    }
                    Text(
                        text = comment.content,
                        color = MaterialTheme.bbibbiScheme.iconSelected,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular
                    )
                }
            }
        }

    }


}