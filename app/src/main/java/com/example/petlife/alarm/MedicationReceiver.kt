package com.example.petlife.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petlife.data.MedicationRepository

class MedicationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(MedicationAlarmScheduler.EXTRA_MEDICATION_ID, -1)
        val medication = MedicationRepository(context).get(id) ?: return
        NotificationHelper.show(context, medication)
        // alarmes exatos não se repetem sozinhos: agenda o disparo de amanhã
        MedicationAlarmScheduler.schedule(context, medication)
    }
}
