package km.clock.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

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
    }
}

class SettingsActivity : AppCompatActivity(), WidgetUpdater {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var preview: FrameLayout? = null
    private var widgetViewCreator: WidgetViewCreator? = null

    // static members
    companion object {
        const val TAG: String = "SettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        preview = findViewById(R.id.preview)
        widgetViewCreator = WidgetViewCreator(this, this)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        updateWidget()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.registerOnSharedPreferenceChangeListener(widgetViewCreator)
        widgetViewCreator!!.onSharedPreferenceChanged(sp, "")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.unregisterOnSharedPreferenceChangeListener(widgetViewCreator)
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

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<Preference>(Pref.Time.SHOW)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Time.FONT)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Time.SIZE)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Time.COLOR)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Time.ALIGN)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Time.FORMAT)?.onPreferenceChangeListener = listener

            findPreference<Preference>(Pref.Date.SHOW)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Date.FONT)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Date.SIZE)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Date.COLOR)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Date.ALIGN)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Date.FORMAT)?.onPreferenceChangeListener = listener

            findPreference<Preference>(Pref.Alarm.SHOW)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Alarm.FONT)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Alarm.SIZE)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Alarm.COLOR)?.onPreferenceChangeListener = listener
            findPreference<Preference>(Pref.Alarm.ALIGN)?.onPreferenceChangeListener = listener
        }

        private var listener: Preference.OnPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, nv ->
                val e = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()

                Log.d(TAG, "${preference.key} = $nv")

                when (preference.key) {
                    Pref.Time.SHOW -> e.putBoolean(Pref.Time.SHOW, nv as Boolean)
                    Pref.Time.FONT -> e.putString(Pref.Time.FONT, nv as String)
                    Pref.Time.SIZE -> e.putInt(Pref.Time.SIZE, nv as Int)
                    Pref.Time.COLOR -> e.putInt(Pref.Time.COLOR, nv as Int)
                    Pref.Time.ALIGN -> e.putString(Pref.Time.ALIGN, nv as String)
                    Pref.Time.FORMAT -> e.putString(Pref.Time.FORMAT, nv as String)
                    Pref.Date.SHOW -> e.putBoolean(Pref.Date.SHOW, nv as Boolean)
                    Pref.Date.FONT -> e.putString(Pref.Date.FONT, nv as String)
                    Pref.Date.SIZE -> e.putInt(Pref.Date.SIZE, nv as Int)
                    Pref.Date.COLOR -> e.putInt(Pref.Date.COLOR, nv as Int)
                    Pref.Date.ALIGN -> e.putString(Pref.Date.ALIGN, nv as String)
                    Pref.Date.FORMAT -> e.putString(Pref.Date.FORMAT, nv as String)
                    Pref.Alarm.SHOW -> e.putBoolean(Pref.Alarm.SHOW, nv as Boolean)
                    Pref.Alarm.FONT -> e.putString(Pref.Alarm.FONT, nv as String)
                    Pref.Alarm.SIZE -> e.putInt(Pref.Alarm.SIZE, nv as Int)
                    Pref.Alarm.COLOR -> e.putInt(Pref.Alarm.COLOR, nv as Int)
                    Pref.Alarm.ALIGN -> e.putString(Pref.Alarm.ALIGN, nv as String)
                }

                e.apply()
                true
            }
    }

}