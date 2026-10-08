package com.example.petlife.domain.scheduler

import com.example.petlife.domain.model.Medication

interface ReminderScheduler {
    fun canScheduleExact(): Boolean

    /** Agenda o próximo disparo diário do medicamento. */
    fun schedule(medication: Medication)
    fun cancel(medicationId: Long)
}
