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
import com.example.petlife.data.Medication

object NotificationHelper {
    private const val CHANNEL_ID = "medication_reminders"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Lembretes de medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Avisa quando o pet precisa tomar o medicamento" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun show(context: Context, medication: Medication) {
        if (!hasPermission(context)) return
        ensureChannel(context)

        val openApp = PendingIntent.getActivity(
            context,
            medication.id,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hora do remédio de ${medication.petName}")
            .setContentText("${medication.medicationName} - ${medication.timeLabel}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(medication.id, notification)
        } catch (_: SecurityException) {
            // permissão revogada entre a checagem e o envio: o lembrete é descartado
        }
    }
}
