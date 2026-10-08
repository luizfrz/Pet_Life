package com.example.petlife.domain.usecase

import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler

class RemoveMedicationUseCase(
    private val repository: MedicationRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke(medicationId: Long) {
        scheduler.cancel(medicationId)
        repository.remove(medicationId)
    }
}
