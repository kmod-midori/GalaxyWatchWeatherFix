package moe.reimu.galaxy.weather.fix

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit
import java.time.LocalTime
import java.util.Locale

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    private val _startTime = MutableStateFlow(getStartTimeInternal())
    val startTime: StateFlow<Int> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow(getEndTimeInternal())
    val endTime: StateFlow<Int> = _endTime.asStateFlow()

    private val _timeRangeEnabled = MutableStateFlow(getTimeRangeEnabledInternal())
    val timeRangeEnabled: StateFlow<Boolean> = _timeRangeEnabled.asStateFlow()

    companion object {
        const val DEFAULT_START_TIME = 8 * 60
        const val DEFAULT_END_TIME = 23 * 60
        const val DEFAULT_TIME_RANGE_ENABLED = true

        private const val KEY_START_TIME = "startTime"
        private const val KEY_END_TIME = "endTime"
        private const val KEY_TIME_RANGE_ENABLED = "timeRangeEnabled"

        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun getStartTimeInternal(): Int {
        return prefs.getInt(KEY_START_TIME, DEFAULT_START_TIME)
    }

    private fun getEndTimeInternal(): Int {
        return prefs.getInt(KEY_END_TIME, DEFAULT_END_TIME)
    }

    private fun getTimeRangeEnabledInternal(): Boolean {
        return prefs.getBoolean(KEY_TIME_RANGE_ENABLED, DEFAULT_TIME_RANGE_ENABLED)
    }

    fun setStartTime(time: LocalTime) {
        val minutes = time.toMinutes()
        prefs.edit { putInt(KEY_START_TIME, minutes) }
        _startTime.value = minutes
    }

    fun setEndTime(time: LocalTime) {
        val minutes = time.toMinutes()
        prefs.edit { putInt(KEY_END_TIME, minutes) }
        _endTime.value = minutes
    }

    fun setTimeRangeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_TIME_RANGE_ENABLED, enabled) }
        _timeRangeEnabled.value = enabled
    }

    fun shouldSendAlarm(time: LocalTime): Boolean {
        if (!timeRangeEnabled.value) return true

        val timeMinutes = time.toMinutes()
        val start = startTime.value
        val end = endTime.value

        return if (end >= start) {
            // e.g. 8:00 to 23:00
            timeMinutes in start..end
        } else {
            // e.g. 8:00 to 1:00, should be next day
            (timeMinutes in start..(23 * 60 + 59)) || (timeMinutes in 0..end)
        }
    }

    fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format(Locale.ROOT, "%02d:%02d", hours, mins)
    }

    private fun LocalTime.toMinutes() = hour * 60 + minute
}
