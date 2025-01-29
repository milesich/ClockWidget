package km.clock.widget

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val TAG: String = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        updateDefaults(requireContext())

        val appWidgetId = requireArguments().getInt("appWidgetId")
        val sp = requireContext().getSharedPreferences(appWidgetId.toString(), MODE_PRIVATE)
        val listener = changeListener(appWidgetId)

        Log.d(TAG, "appWidgetId = $appWidgetId")

        val pt = Pref.Time
        val pd = Pref.Date
        val pa = Pref.Alarm
        val dt = Default.Time
        val dd = Default.Date
        val da = Default.Alarm

        findPreference<ListPreference>(Pref.FONT)?.apply {
            onPreferenceChangeListener = listener
            value = sp.getString(Pref.FONT, Default.FONT)
        }

        mapOf(
            pt.SHOW to dt.SHOW, pd.SHOW to dd.SHOW, pa.SHOW to da.SHOW
        ).forEach { (k, v) ->
            findPreference<SwitchPreferenceCompat>(k)?.apply {
                onPreferenceChangeListener = listener
                isChecked = sp.getBoolean(k, v)
            }
        }

        mapOf(
            pt.SIZE to dt.SIZE, pd.SIZE to dd.SIZE, pa.SIZE to da.SIZE
        ).forEach { (k, v) ->
            findPreference<SeekBarPreference>(k)?.apply {
                onPreferenceChangeListener = listener
                value = sp.getInt(k, v)
            }
        }

        mapOf(
            pt.COLOR to dt.COLOR, pd.COLOR to dd.COLOR, pa.COLOR to da.COLOR
        ).forEach { (k, v) ->
            findPreference<ColorPreferenceCompat>(k)?.apply {
                onPreferenceChangeListener = listener
                saveValue(sp.getInt(k, v))
            }
        }

        mapOf(
            pt.ALIGN to dt.ALIGN, pd.ALIGN to dd.ALIGN, pa.ALIGN to da.ALIGN
        ).forEach { (k, v) ->
            findPreference<ListPreference>(k)?.apply {
                onPreferenceChangeListener = listener
                value = sp.getString(k, v)
            }
        }

        mapOf(
            pt.FORMAT to dt.FORMAT, pd.FORMAT to dd.FORMAT, pa.FORMAT to da.FORMAT
        ).forEach { (k, v) ->
            findPreference<EditTextPreference>(k)?.apply {
                onPreferenceChangeListener = listener
                text = sp.getString(k, v)
            }
        }
    }

    private fun changeListener(id: Int): Preference.OnPreferenceChangeListener {
        return Preference.OnPreferenceChangeListener { preference, newValue ->
            val e = requireContext().getSharedPreferences(id.toString(), MODE_PRIVATE).edit()

            Log.d(TAG, "${preference.key} = $newValue")

            when (preference.key) {
                Pref.FONT -> e.putString(Pref.FONT, newValue as String)
                Pref.Time.SHOW -> e.putBoolean(Pref.Time.SHOW, newValue as Boolean)
                Pref.Time.SIZE -> e.putInt(Pref.Time.SIZE, newValue as Int)
                Pref.Time.COLOR -> e.putInt(Pref.Time.COLOR, newValue as Int)
                Pref.Time.ALIGN -> e.putString(Pref.Time.ALIGN, newValue as String)
                Pref.Time.FORMAT -> e.putString(Pref.Time.FORMAT, newValue as String)
                Pref.Date.SHOW -> e.putBoolean(Pref.Date.SHOW, newValue as Boolean)
                Pref.Date.SIZE -> e.putInt(Pref.Date.SIZE, newValue as Int)
                Pref.Date.COLOR -> e.putInt(Pref.Date.COLOR, newValue as Int)
                Pref.Date.ALIGN -> e.putString(Pref.Date.ALIGN, newValue as String)
                Pref.Date.FORMAT -> e.putString(Pref.Date.FORMAT, newValue as String)
                Pref.Alarm.SHOW -> e.putBoolean(Pref.Alarm.SHOW, newValue as Boolean)
                Pref.Alarm.SIZE -> e.putInt(Pref.Alarm.SIZE, newValue as Int)
                Pref.Alarm.COLOR -> e.putInt(Pref.Alarm.COLOR, newValue as Int)
                Pref.Alarm.ALIGN -> e.putString(Pref.Alarm.ALIGN, newValue as String)
                Pref.Alarm.FORMAT -> e.putString(Pref.Alarm.FORMAT, newValue as String)
            }

            e.apply()
            true
        }
    }
}
