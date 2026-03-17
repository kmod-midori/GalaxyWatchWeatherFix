@file:OptIn(ExperimentalHorologistApi::class)

package moe.reimu.galaxy.weather.fix.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import moe.reimu.galaxy.weather.fix.AlarmReceiver
import moe.reimu.galaxy.weather.fix.SettingsManager
import moe.reimu.galaxy.weather.fix.presentation.theme.GalaxyWatchWeatherFixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    GalaxyWatchWeatherFixTheme {
        AppScaffold {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val columnState = rememberScalingLazyListState()
    val context = LocalContext.current

    val settingsManager = SettingsManager.getInstance(context)
    val startTime by settingsManager.startTime.collectAsState()
    val endTime by settingsManager.endTime.collectAsState()
    val timeRangeEnabled by settingsManager.timeRangeEnabled.collectAsState()

    ScreenScaffold(scrollState = columnState) { contentPadding ->
        ScalingLazyColumn(contentPadding = contentPadding, state = columnState) {
            item {
                ListHeader(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Galaxy Watch Weather Fix")
                }
            }
            item {
                Button(onClick = {
                    AlarmReceiver.sendRefreshIntent(context)
                    Toast.makeText(context, "Broadcast sent", Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Send refresh broadcast")
                }
            }
            item {
                Button(onClick = {
                    AlarmReceiver.scheduleAlarm(context)
                    Toast.makeText(context, "Alarm scheduled", Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Schedule alarm")
                }
            }

            item {
                ToggleChip(
                    checked = timeRangeEnabled,
                    onCheckedChanged = { settingsManager.setTimeRangeEnabled(it) },
                    label = "Time range",
                    toggleControl = ToggleChipToggleControl.Switch,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (timeRangeEnabled) {
                item {
                    TextButton(
                        onClick = {
                            context.startActivity(
                                TimePickerActivity.createIntent(
                                    context, TimePickerActivity.TIME_TYPE_START
                                )
                            )
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start: ${settingsManager.formatTime(startTime)}")
                    }
                }
                item {
                    TextButton(
                        onClick = {
                            context.startActivity(
                                TimePickerActivity.createIntent(
                                    context, TimePickerActivity.TIME_TYPE_END
                                )
                            )
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("End: ${settingsManager.formatTime(endTime)}")
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
