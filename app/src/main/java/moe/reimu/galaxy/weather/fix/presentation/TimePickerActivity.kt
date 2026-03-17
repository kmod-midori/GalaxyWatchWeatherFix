package moe.reimu.galaxy.weather.fix.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.horologist.composables.TimePicker
import com.google.android.horologist.compose.layout.AppScaffold
import moe.reimu.galaxy.weather.fix.SettingsManager
import moe.reimu.galaxy.weather.fix.presentation.theme.GalaxyWatchWeatherFixTheme
import java.time.LocalTime

class TimePickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val timeType = intent.getStringExtra(EXTRA_TIME_TYPE) ?: ""

        setContent {
            GalaxyWatchWeatherFixTheme {
                AppScaffold {
                    TimePickerScreen(timeType = timeType, onBack = { finish() })
                }
            }
        }
    }

    companion object {
        const val EXTRA_TIME_TYPE = "time_type"
        const val TIME_TYPE_START = "start"
        const val TIME_TYPE_END = "end"

        fun createIntent(context: Context, timeType: String): Intent {
            return Intent(context, TimePickerActivity::class.java).apply {
                putExtra(EXTRA_TIME_TYPE, timeType)
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun TimePickerScreen(timeType: String, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsManager = SettingsManager.getInstance(context)

    val (currentTime, onTimeConfirm) = when (timeType) {
        TimePickerActivity.TIME_TYPE_START -> {
            val startTime by settingsManager.startTime.collectAsState()
            val initialTime = LocalTime.of(startTime / 60, startTime % 60)
            initialTime to { localTime: LocalTime ->
                settingsManager.setStartTime(localTime)
                onBack()
            }
        }

        TimePickerActivity.TIME_TYPE_END -> {
            val endTime by settingsManager.endTime.collectAsState()
            val initialTime = LocalTime.of(endTime / 60, endTime % 60)
            initialTime to { localTime: LocalTime ->
                settingsManager.setEndTime(localTime)
                onBack()
            }
        }

        else -> throw IllegalArgumentException()
    }

    TimePicker(
        time = currentTime,
        showSeconds = false,
        onTimeConfirm = onTimeConfirm
    )
}
