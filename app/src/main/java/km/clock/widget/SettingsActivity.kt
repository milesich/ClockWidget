package km.clock.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit

object Pref {
    object Time {
        const val SHOW = "time_show"
        const val FONT = "time_font"
        const val SIZE = "time_size"
        const val COLOR = "time_color"
        const val ALIGN = "time_align"
        const val FORMAT = "time_format"
    }

    object Date {
        const val SHOW = "date_show"
        const val FONT = "date_font"
        const val SIZE = "date_size"
        const val COLOR = "date_color"
        const val ALIGN = "date_align"
        const val FORMAT = "date_format"
    }

    object Alarm {
        const val SHOW = "alarm_show"
        const val FONT = "alarm_font"
        const val SIZE = "alarm_size"
        const val COLOR = "alarm_color"
        const val ALIGN = "alarm_align"
        const val FORMAT = "alarm_format"
    }
}

class SettingsActivity : AppCompatActivity(R.layout.settings_activity), WidgetUpdater {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var preview: FrameLayout? = null
    private var widgetViewCreator: WidgetViewCreator? = null

    companion object {
        const val TAG: String = "SettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        Log.d(TAG, "appWidgetId = $appWidgetId")

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        preview = findViewById(R.id.preview)

        if (savedInstanceState == null) {
            val bundle = bundleOf("appWidgetId" to appWidgetId)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SettingsFragment>(R.id.settings, args = bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume $appWidgetId")

        widgetViewCreator = WidgetViewCreator(this, this)
        val sp = getSharedPreferences(appWidgetId.toString(), MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener(widgetViewCreator)
        widgetViewCreator!!.onSharedPreferenceChanged(sp, "")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause $appWidgetId")

        val sp = getSharedPreferences(appWidgetId.toString(), MODE_PRIVATE)
        sp.unregisterOnSharedPreferenceChangeListener(widgetViewCreator)
        widgetViewCreator = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.savebtn) {
            val views = widgetViewCreator!!.createWidgetRemoteView()
            val appWidgetManager = AppWidgetManager.getInstance(this)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateWidget() {
        val views = widgetViewCreator!!.createWidgetRemoteView()
        preview!!.removeAllViews()
        val previewView = views.apply(this, preview)
        preview!!.addView(previewView)
    }
}