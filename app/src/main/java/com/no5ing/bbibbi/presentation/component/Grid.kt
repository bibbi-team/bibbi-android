package com.no5ing.bbibbi.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.util.dpToPx

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: @Composable () -> Unit
) {
    val gap = 3.dp.dpToPx().toInt()
    val verticalGap = 16.dp.dpToPx().toInt()
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = (constraints.maxWidth - gap) / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        // Measure each item with these constraints
        val placeables = measurables.map { it.measure(itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull()?.plus(verticalGap) ?: constraints.minHeight)
            .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                val row = index / columns
                placeable.placeRelative(
                    x = column * itemWidth + if (column > 0) gap else 0,
                    y = columnY[column] + if (row > 0) verticalGap else 0,
                )
                columnY[column] += placeable.height
            }
        }
    }
}