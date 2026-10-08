package com.example.petlife.domain.usecase

import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.notification.MedicationNotifier
import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler

class AddMedicationUseCase(
    private val repository: MedicationRepository,
    private val scheduler: ReminderScheduler,
    private val notifier: MedicationNotifier
) {
    /** Valida, persiste, agenda o alarme e avisa o usuário. Falha com [IllegalArgumentException] se inválido. */
    suspend operator fun invoke(
        petName: String,
        medicationName: String,
        hour: Int,
        minute: Int
    ): Result<Medication> {
        if (medicationName.isBlank()) return Result.failure(IllegalArgumentException("Informe o medicamento"))
        if (hour !in 0..23 || minute !in 0..59) return Result.failure(IllegalArgumentException("Horário inválido"))

        return runCatching {
            val saved = repository.add(
                Medication(
                    petName = petName.trim(),
                    medicationName = medicationName.trim(),
                    hour = hour,
                    minute = minute
                )
            )
            scheduler.schedule(saved)
            notifier.showScheduled()
            saved
        }
    }
}
