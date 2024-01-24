package com.no5ing.bbibbi

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.no5ing.bbibbi.presentation.ui.widget.AppWidget

class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget(
        smallImageId = R.drawable.widget_small,
        largeImageId = R.drawable.widget_large
    )
}