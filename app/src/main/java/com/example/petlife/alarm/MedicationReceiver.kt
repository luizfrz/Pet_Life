package com.example.petlife.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petlife.PetLifeApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(AlarmReminderScheduler.EXTRA_MEDICATION_ID, -1L)
        if (id < 0) return
        val container = (context.applicationContext as PetLifeApp).container

        val pending = goAsync() // o acesso ao Room é assíncrono; mantém o receiver vivo até terminar
        CoroutineScope(Dispatchers.IO).launch {
            try {
                container.triggerReminder(id)
            } finally {
                pending.finish()
            }
        }
    }
}
