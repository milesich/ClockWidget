package km.clock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager

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
            Log.d("CWP", action)
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Get all ids
        val thisWidget = ComponentName(context, ClockWidget::class.java)

        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (widgetId in allWidgetIds) {
            redrawWidgetFromData(context, appWidgetManager, widgetId)
        }
    }

    private fun redrawWidgetFromData(
        context: Context, appWidgetManager: AppWidgetManager, widgetId: Int
    ) {
        widgetViewCreator = WidgetViewCreator(context, this)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        widgetViewCreator!!.onSharedPreferenceChanged(sharedPreferences, "")
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
