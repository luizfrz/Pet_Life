package com.example.petlife.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petlife.PetLifeApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Alarmes são apagados no reboot e podem ficar defasados em mudanças de hora/fuso; reagenda todos. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                val container = (context.applicationContext as PetLifeApp).container
                val pending = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        container.rescheduleAll()
                    } finally {
                        pending.finish()
                    }
                }
            }
        }
    }
}
