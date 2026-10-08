package com.example.petlife.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.petlife.MainActivity
import com.example.petlife.R
import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.notification.MedicationNotifier

/** Implementação de [MedicationNotifier] sobre as notificações do sistema. */
class SystemMedicationNotifier(context: Context) : MedicationNotifier {
    private val context = context.applicationContext

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Lembretes de medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Avisa quando o pet precisa tomar o medicamento" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun showScheduled() {
        notify(
            id = SCHEDULED_NOTIFICATION_ID,
            title = "Você tem um medicamento agendado",
            text = null
        )
    }

    override fun showReminder(medication: Medication) {
        notify(
            id = medication.id.toInt(),
            title = if (medication.petName.isBlank()) "Hora do remédio" else "Hora do remédio de ${medication.petName}",
            text = "${medication.medicationName} - ${medication.timeLabel}",
            category = NotificationCompat.CATEGORY_REMINDER
        )
    }

    private fun notify(id: Int, title: String, text: String?, category: String? = null) {
        if (!hasPermission(context)) return
        ensureChannel()

        val openApp = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .apply { text?.let(::setContentText) }
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .apply { category?.let(::setCategory) }
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // permissão revogada entre a checagem e o envio: a notificação é descartada
        }
    }

    companion object {
        private const val CHANNEL_ID = "medication_reminders"
        private const val SCHEDULED_NOTIFICATION_ID = 0 // ids de lembretes começam em 1 (autoGenerate)

        fun hasPermission(context: Context): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }
}
