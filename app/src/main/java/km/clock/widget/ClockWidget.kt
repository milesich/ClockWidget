package km.clock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ClockWidgetConfigureActivity]
 */
class ClockWidget : AppWidgetProvider(), WidgetUpdater {
    private var widgetViewCreator: WidgetViewCreator? = null

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action != null && action == "android.app.action.NEXT_ALARM_CLOCK_CHANGED") {
            Log.d("ClockWidget", action)
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            redrawWidgetFromData(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            val sp = context.getSharedPreferences(appWidgetId.toString(), MODE_PRIVATE)
            sp.edit().clear().apply()
        }
    }

    private fun redrawWidgetFromData(
        context: Context, appWidgetManager: AppWidgetManager, widgetId: Int
    ) {
        val sp = context.getSharedPreferences(widgetId.toString(), MODE_PRIVATE)
        widgetViewCreator = WidgetViewCreator(context, this)
        widgetViewCreator!!.onSharedPreferenceChanged(sp, "")
        val views = widgetViewCreator!!.createWidgetRemoteView()
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    override fun updateWidget() {
        // not used here
    }
}

interface WidgetUpdater {
    fun updateWidget()
}
