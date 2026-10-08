package com.example.petlife.domain.usecase

import com.example.petlife.domain.scheduler.ReminderScheduler

class CanScheduleExactAlarmsUseCase(private val scheduler: ReminderScheduler) {
    operator fun invoke(): Boolean = scheduler.canScheduleExact()
}
