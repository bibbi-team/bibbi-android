package com.no5ing.bbibbi.presentation.ui.feature.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.common.component.ModalBottomSheet
import com.no5ing.bbibbi.presentation.ui.common.component.SheetValue
import com.no5ing.bbibbi.presentation.ui.common.component.rememberModalBottomSheetState
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostCommentDialog(
    postId: String,
    isEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    textBoxFocus: FocusRequester = remember { FocusRequester() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
  //  val coroutineScope = rememberCoroutineScope()
    if(isEnabled.value) {

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var keyboardExpanded by remember { mutableStateOf(false) }
      //  var isAnimating by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            //sheetState.expand()
            Timber.d("Recomp")
        }

//        LaunchedEffect(keyboardExpanded) {
//            CoroutineScope(AndroidUiDispatcher.Main).launch {
//                Timber.d("Keyboard Expanded")
//                if(keyboardExpanded) {
//                    Timber.d("expandeing")
//                    sheetState.expand()
//                    Timber.d("expanded!")
//                }
//            }
//
//        }

        Box(
            modifier = Modifier
            ///    .nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            val sheetState = rememberModalBottomSheetState(
                //  skipPartiallyExpanded = keyboardExpanded
                //  confirmValueChange = {!isAnimating}
                confirmValueChange = {
                    if(it == SheetValue.PartiallyExpanded) {
                        textBoxFocus.freeFocus()
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    }
                    true
                }
            )
            ModalBottomSheet(
                onDismissRequest = { isEnabled.value = false },
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier
                    .navigationBarsPadding(),
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                containerColor = MaterialTheme.bbibbiScheme.backgroundPrimary,
                dragHandle = {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(width = 32.dp, height = 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.bbibbiScheme.button)
                        )}

                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "댓글", color = MaterialTheme.bbibbiScheme.textPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 1.dp, color = MaterialTheme.bbibbiScheme.gray600)
                }


                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    LazyColumn(
                      //  state = lazyColumn,
                        modifier = Modifier
                            .fillMaxSize()

                        //.nestedScroll(scroll)
                    ) {
                        items(50) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Hello $it"
                            )
                        }
                    }
                    Texx(modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = 0,
                                y = -sheetState
                                    .requireOffset()
                                    .toInt()
                            )
                        },
                        onFocusChanged = {
                            if(it.isFocused) {
                                Timber.d("State : ${it}")
                                keyboardExpanded = true
                            }
                        },
                        focusRequester = textBoxFocus,
                    )
                }
            }
        }



    }
}
@Composable
fun Texx(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onFocusChanged: (FocusState) -> Unit,
) {
    Column(
        modifier = modifier
            .background(Color.Red)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = "hello world",
                modifier = Modifier
                    .onFocusEvent {
                        Timber.d("FE : ${it}")
                    }
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onFocusChanged(it)
                    },
                onValueChange = { nextValue ->

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.bbibbiScheme.white
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
                maxLines = 1,
            )
            Icon(
                painter = painterResource(id = R.drawable.clear_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {

                    },
                tint = MaterialTheme.bbibbiScheme.icon
            )
        }

    }
}