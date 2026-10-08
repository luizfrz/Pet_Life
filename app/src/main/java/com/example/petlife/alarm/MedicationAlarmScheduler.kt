package com.example.petlife.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.petlife.data.Medication
import com.example.petlife.data.MedicationRepository
import java.util.Calendar

object MedicationAlarmScheduler {
    const val EXTRA_MEDICATION_ID = "medication_id"

    fun canScheduleExact(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    /** Agenda o próximo disparo (hoje se o horário ainda não passou, senão amanhã). */
    fun schedule(context: Context, medication: Medication) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAt = nextTriggerMillis(medication.hour, medication.minute)
        val pendingIntent = pendingIntent(context, medication.id)

        if (canScheduleExact(context)) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                return
            } catch (_: SecurityException) {
                // permissão revogada entre a checagem e o agendamento: cai no alarme inexato
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun cancel(context: Context, medicationId: Int) {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context, medicationId))
    }

    fun rescheduleAll(context: Context) {
        MedicationRepository(context).getAll().forEach { schedule(context, it) }
    }

    private fun pendingIntent(context: Context, medicationId: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            medicationId,
            Intent(context, MedicationReceiver::class.java).putExtra(EXTRA_MEDICATION_ID, medicationId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis
    }
}
