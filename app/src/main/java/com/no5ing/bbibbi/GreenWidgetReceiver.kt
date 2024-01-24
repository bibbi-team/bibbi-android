package com.no5ing.bbibbi

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.no5ing.bbibbi.presentation.ui.widget.AppWidget

class GreenWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget(
        uid = "green",
        smallImageId = R.drawable.widget_small_green,
        largeImageId = R.drawable.widget_large_green
    )
}