package com.no5ing.bbibbi.presentation.feature.view.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertDialog(
    enabledState: State<Boolean> = remember { mutableStateOf(false) },
    title: String,
    description: String,
    confirmRequest: () -> Unit = {},
    dismissRequest: () -> Unit = {
        if (enabledState is MutableState) enabledState.value = false
    },
    cancelRequest: () -> Unit = {
        if (enabledState is MutableState) enabledState.value = false
    },
    confirmMessage: String = stringResource(id = R.string.dialog_confirm),
    cancelMessage: String = stringResource(id = R.string.dialog_cancel),
    hasCancel: Boolean = true,
) {
    if (enabledState.value) {
        AlertDialog(
            onDismissRequest = cancelRequest,
            modifier = Modifier,
            properties = DialogProperties()
        ) {
            AlertDialogContent(
                buttons = {
                    AlertDialogFlowRow(
                        mainAxisSpacing = ButtonsMainAxisSpacing,
                        crossAxisSpacing = ButtonsCrossAxisSpacing
                    ) {
                        if (hasCancel) {
                            Button(
                                onClick = dismissRequest,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.bbibbiScheme.button
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(width = 126.dp, height = 44.dp)
                            ) {
                                Text(
                                    cancelMessage,
                                    style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                    color = Color(0xffFFFFFF)
                                )
                            }
                        }
                        Button(
                            onClick = confirmRequest,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.bbibbiScheme.mainYellow
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(width = 126.dp, height = 44.dp)
                        ) {
                            Text(
                                confirmMessage,
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = Color(0xff242427)
                            )
                        }
                    }
                },
                icon = null,
                title = {
                    Text(
                        title,
                        color = MaterialTheme.bbibbiScheme.iconSelected,
                        style = MaterialTheme.bbibbiTypo.headTwoBold,
                    )
                },
                text = {
                    Text(
                        description,
                        color = MaterialTheme.bbibbiScheme.textSecondary,
                        style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                        textAlign = TextAlign.Center,
                    )
                },
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                iconContentColor = AlertDialogDefaults.iconContentColor,
                titleContentColor = AlertDialogDefaults.titleContentColor,
                textContentColor = AlertDialogDefaults.textContentColor,
                modifier = Modifier.padding(0.dp)
            )

        }
    }
}

@Composable
internal fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: @Composable (() -> Unit)?,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
    textPadding: PaddingValues = TextPadding,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingValues(top = 24.dp, bottom = 16.dp, start = 24.dp, end = 24.dp))
        ) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier
                            .padding(IconPadding)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    val textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    ProvideTextStyle(textStyle) {
                        Box(
                            // Align the title to the center when an icon is present.
                            Modifier
                                .padding(TitlePadding)
                                .align(
                                    Alignment.CenterHorizontally
                                )
                        ) {
                            title()
                        }
                    }
                }
            }
            text?.let {
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    val textStyle =
                        MaterialTheme.bbibbiTypo.bodyOneRegular
                    ProvideTextStyle(textStyle) {
                        Box(
                            Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(textPadding)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            text()
                        }
                    }
                }
            }
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                buttons()
            }
        }
    }
}

@Composable
internal fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() +
                    placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        for (measurable in measurables) {
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.forEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                            if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize, childrenMainAxisSizes,
                        layoutDirection, mainAxisPositions
                    )
                }
                placeables.forEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}

internal val DialogMinWidth = 280.dp
internal val DialogMaxWidth = 560.dp

// Paddings for each of the dialog's parts.
private val DialogPadding = PaddingValues(all = 24.dp)
internal val IconPadding = PaddingValues(bottom = 16.dp)
internal val TitlePadding = PaddingValues(bottom = 16.dp)
internal val TextPadding = PaddingValues(bottom = 24.dp)
internal val ButtonsMainAxisSpacing = 8.dp
internal val ButtonsCrossAxisSpacing = 12.dp


@Composable
fun AlbumCameraSelectDialog(
    enabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onAlbum: () -> Unit = {},
    onCamera: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_image_upload_title),
        description = stringResource(id = R.string.dialog_image_upload_message),
        confirmMessage = stringResource(id = R.string.dialog_image_upload_camera),
        cancelMessage = stringResource(id = R.string.dialog_image_upload_album),
        enabledState = enabledState,
        confirmRequest = onCamera,
        dismissRequest = onAlbum,
        cancelRequest = onCancel,
    )
}