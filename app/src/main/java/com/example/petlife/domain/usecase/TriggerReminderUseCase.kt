package com.example.petlife.domain.usecase

import com.example.petlife.domain.notification.MedicationNotifier
import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler

/** Executado quando o alarme dispara: notifica e agenda o disparo do dia seguinte. */
class TriggerReminderUseCase(
    private val repository: MedicationRepository,
    private val scheduler: ReminderScheduler,
    private val notifier: MedicationNotifier
) {
    suspend operator fun invoke(medicationId: Long) {
        val medication = repository.get(medicationId) ?: return
        repository.setTaken(medicationId, false) // nova dose: desmarca o "já tomou"
        notifier.showReminder(medication)
        scheduler.schedule(medication)
    }
}
