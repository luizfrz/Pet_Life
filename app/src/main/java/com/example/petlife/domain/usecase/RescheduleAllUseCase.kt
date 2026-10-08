package com.example.petlife.domain.usecase

import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler

/** Reagenda todos os alarmes (após reboot, atualização do app ou mudança de hora/fuso). */
class RescheduleAllUseCase(
    private val repository: MedicationRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke() = repository.getAll().forEach(scheduler::schedule)
}
