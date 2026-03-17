package moe.reimu.galaxy.weather.fix

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalTime
import java.util.Calendar
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive ${intent.action}")
        when (intent.action) {
            "android.intent.action.BOOT_COMPLETED" -> {
                scheduleAlarm(context)
            }

            ACTION -> {
                val settingsManager = SettingsManager.getInstance(context)

                if (settingsManager.shouldSendAlarm(LocalTime.now())) {
                    Log.i(TAG, "Sending refresh intent")
                    sendRefreshIntent(context)
                } else {
                    Log.i(TAG, "Got shouldSendAlarm = false, skipping refresh")
                }
            }
        }
    }

    companion object {
        private const val TAG = "AlarmReceiver"
        private const val ACTION = "moe.reimu.galaxy.weather.fix.AUTO_REFRESH"

        fun sendRefreshIntent(context: Context) {
            val intent = Intent("com.samsung.android.weather.intent.action.AUTOREFRESH").apply {
                `package` = "com.samsung.android.watch.weather"
            }
            context.sendBroadcast(intent)
        }

        fun scheduleAlarm(context: Context) {
            val intent = Intent(ACTION).apply { `package` = context.packageName }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1123,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 10.seconds.inWholeMilliseconds,
                30.minutes.inWholeMilliseconds,
                pendingIntent,
            )
        }
    }
}