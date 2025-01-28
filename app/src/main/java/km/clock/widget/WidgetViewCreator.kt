package km.clock.widget

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RemoteViews
import com.google.android.material.color.MaterialColors


object Default {
    object Time {
        const val SHOW = true
        const val FONT = "default"
        const val SIZE = 42
        const val ALIGN = Gravity.CENTER.toString()
        var COLOR = Color.WHITE
        var FORMAT = "H:mm"
    }

    object Date {
        const val SHOW = true
        const val FONT = "default"
        const val SIZE = 22
        const val ALIGN = Gravity.CENTER.toString()
        var COLOR = Color.WHITE
        var FORMAT = "EEEE, MMM d"
    }

    object Alarm {
        const val SHOW = true
        const val FONT = "default"
        const val SIZE = 22
        const val ALIGN = Gravity.CENTER.toString()
        var COLOR = Color.WHITE
        var FORMAT = "E HH:mm"
    }
}

class WidgetViewCreator(private val context: Context, private val updater: WidgetUpdater) :
    OnSharedPreferenceChangeListener {

    init {
        if (!DateFormat.is24HourFormat(context)) {
            Default.Time.FORMAT = "h:mm"
            Default.Alarm.FORMAT = "E hh:mm a"
        }

        val color = MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSurface,
            Color.WHITE
        )

        Default.Time.COLOR = color
        Default.Date.COLOR = color
        Default.Alarm.COLOR = color
    }

    companion object {
        var timeShow: Boolean = Default.Time.SHOW
        var timeFont: String = Default.Time.FONT
        var timeSize: Int = Default.Time.SIZE
        var timeColor: Int = Default.Time.COLOR
        var timeAlign: String = Default.Time.ALIGN
        var timeFormat: String = Default.Time.FORMAT

        var dateShow: Boolean = Default.Date.SHOW
        var dateFont: String = Default.Date.FONT
        var dateSize: Int = Default.Date.SIZE
        var dateColor: Int = Default.Date.COLOR
        var dateAlign: String = Default.Date.ALIGN
        var dateFormat: String = Default.Date.FORMAT

        var alarmShow: Boolean = Default.Alarm.SHOW
        var alarmFont: String = Default.Alarm.FONT
        var alarmSize: Int = Default.Alarm.SIZE
        var alarmColor: Int = Default.Alarm.COLOR
        var alarmAlign: String = Default.Alarm.ALIGN
        var alarmFormat: String = Default.Alarm.FORMAT
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String?) {
        timeShow = sp.getBoolean(Pref.Time.SHOW, Default.Time.SHOW)
        timeFont = sp.getString(Pref.Time.FONT, Default.Time.FONT)!!
        timeSize = sp.getInt(Pref.Time.SIZE, Default.Time.SIZE)
        timeColor = sp.getInt(Pref.Time.COLOR, Default.Time.COLOR)
        timeAlign = sp.getString(Pref.Time.ALIGN, Default.Time.ALIGN)!!
        timeFormat = sp.getString(Pref.Time.FORMAT, Default.Time.FORMAT)!!

        dateShow = sp.getBoolean(Pref.Date.SHOW, Default.Date.SHOW)
        dateFont = sp.getString(Pref.Date.FONT, Default.Date.FONT)!!
        dateSize = sp.getInt(Pref.Date.SIZE, Default.Date.SIZE)
        dateColor = sp.getInt(Pref.Date.COLOR, Default.Date.COLOR)
        dateAlign = sp.getString(Pref.Date.ALIGN, Default.Date.ALIGN)!!
        dateFormat = sp.getString(Pref.Date.FORMAT, Default.Date.FORMAT)!!

        alarmShow = sp.getBoolean(Pref.Alarm.SHOW, Default.Alarm.SHOW)
        alarmFont = sp.getString(Pref.Alarm.FONT, Default.Alarm.FONT)!!
        alarmSize = sp.getInt(Pref.Alarm.SIZE, Default.Alarm.SIZE)
        alarmColor = sp.getInt(Pref.Alarm.COLOR, Default.Alarm.COLOR)
        alarmAlign = sp.getString(Pref.Alarm.ALIGN, Default.Alarm.ALIGN)!!
        alarmFormat = sp.getString(Pref.Alarm.FORMAT, Default.Alarm.FORMAT)!!

        updater.updateWidget()
    }

    fun createWidgetRemoteView(): RemoteViews {
        val views = RemoteViews(context.packageName, layoutResource)

        views.setViewVisibility(R.id.time, View.GONE)
        views.setViewVisibility(R.id.date, View.GONE)
        views.setViewVisibility(R.id.alarmView, View.GONE)

        if (timeShow) {
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setCharSequence(R.id.time, "setFormat24Hour", timeFormat)
            views.setCharSequence(R.id.time, "setFormat12Hour", timeFormat)
            views.setTextViewTextSize(R.id.time, TypedValue.COMPLEX_UNIT_SP, timeSize.toFloat())
            views.setTextColor(R.id.time, timeColor)
            views.setInt(R.id.time, "setGravity", timeAlign.toInt())
        }

        if (dateShow) {
            views.setViewVisibility(R.id.date, View.VISIBLE)
            views.setCharSequence(R.id.date, "setFormat24Hour", dateFormat)
            views.setCharSequence(R.id.date, "setFormat12Hour", dateFormat)
            views.setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, dateSize.toFloat())
            views.setTextColor(R.id.date, dateColor)
            views.setInt(R.id.dateAlarmView, "setGravity", dateAlign.toInt())
        }

        if (alarmShow) {
            val alarmText = nextAlarmText
            if (alarmText.isNotEmpty()) {
                val csl = ColorStateList.valueOf(alarmColor)
                views.setViewVisibility(R.id.alarmView, View.VISIBLE)
                views.setTextViewTextSize(
                    R.id.alarm,
                    TypedValue.COMPLEX_UNIT_SP,
                    alarmSize.toFloat()
                )
                views.setTextColor(R.id.alarm, alarmColor)
                views.setColorStateList(R.id.alarmIcon, "setImageTintList", csl)
                views.setTextViewText(R.id.alarm, alarmText)
                views.setViewLayoutWidth(
                    R.id.alarmIcon,
                    alarmSize.toFloat(),
                    TypedValue.COMPLEX_UNIT_SP
                )
                views.setInt(R.id.dateAlarmView, "setGravity", alarmAlign.toInt())
            }
        }

        return views
    }

    private val layoutResource: Int
        get() {
            return R.layout.widget_layout_default
        }

    private val nextAlarmText: String
        get() {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val aci = am.nextAlarmClock ?: return ""
            return DateFormat.format(alarmFormat, aci.triggerTime) as String
        }
}
