package com.example.petlife.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.scheduler.ReminderScheduler
import java.util.Calendar

/** Implementação de [ReminderScheduler] sobre o [AlarmManager] do Android. */
class AlarmReminderScheduler(context: Context) : ReminderScheduler {
    private val context = context.applicationContext
    private val alarmManager = this.context.getSystemService(AlarmManager::class.java)

    override fun canScheduleExact(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    override fun schedule(medication: Medication) {
        val triggerAt = nextTriggerMillis(medication.hour, medication.minute)
        val pendingIntent = pendingIntent(medication.id)

        if (canScheduleExact()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                return
            } catch (_: SecurityException) {
                // permissão revogada entre a checagem e o agendamento: cai no alarme inexato
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    override fun cancel(medicationId: Long) {
        alarmManager.cancel(pendingIntent(medicationId))
    }

    private fun pendingIntent(medicationId: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            medicationId.toInt(),
            Intent(context, MedicationReceiver::class.java).putExtra(EXTRA_MEDICATION_ID, medicationId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    /** Próximo horário `hour:minute`: hoje se ainda não passou, senão amanhã. */
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

    companion object {
        const val EXTRA_MEDICATION_ID = "medication_id"
    }
}
