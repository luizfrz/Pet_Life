package com.example.petlife.domain.usecase

import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler

class UpdateMedicationUseCase(
    private val repository: MedicationRepository,
    private val scheduler: ReminderScheduler
) {
    /**
     * Altera nome, pet e horário de um agendamento existente e reagenda o alarme
     * (o mesmo id substitui o disparo anterior). A alteração inicia uma nova dose: "já tomou" é desmarcado.
     */
    suspend operator fun invoke(
        id: Long,
        petName: String,
        medicationName: String,
        hour: Int,
        minute: Int
    ): Result<Medication> {
        if (medicationName.isBlank()) return Result.failure(IllegalArgumentException("Informe o medicamento"))
        if (hour !in 0..23 || minute !in 0..59) return Result.failure(IllegalArgumentException("Horário inválido"))

        return runCatching {
            val current = repository.get(id) ?: throw IllegalArgumentException("Agendamento não encontrado")
            val updated = current.copy(
                petName = petName.trim(),
                medicationName = medicationName.trim(),
                hour = hour,
                minute = minute,
                taken = false
            )
            repository.update(updated)
            scheduler.schedule(updated)
            updated
        }
    }
}
