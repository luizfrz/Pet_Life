package com.example.petlife.di

import android.content.Context
import androidx.room.Room
import com.example.petlife.alarm.AlarmReminderScheduler
import com.example.petlife.alarm.SystemMedicationNotifier
import com.example.petlife.data.local.PetLifeDatabase
import com.example.petlife.data.repository.MedicationRepositoryImpl
import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.usecase.AddMedicationUseCase
import com.example.petlife.domain.usecase.CanScheduleExactAlarmsUseCase
import com.example.petlife.domain.usecase.ObserveMedicationsUseCase
import com.example.petlife.domain.usecase.RemoveMedicationUseCase
import com.example.petlife.domain.usecase.RescheduleAllUseCase
import com.example.petlife.domain.usecase.SetMedicationTakenUseCase
import com.example.petlife.domain.usecase.TriggerReminderUseCase
import com.example.petlife.domain.usecase.UpdateMedicationUseCase

/** Injeção de dependências manual: único lugar que conhece as implementações concretas de cada camada. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database by lazy {
        Room.databaseBuilder(appContext, PetLifeDatabase::class.java, "petlife.db").build()
    }

    val notifier by lazy { SystemMedicationNotifier(appContext) }
    private val scheduler by lazy { AlarmReminderScheduler(appContext) }
    private val repository: MedicationRepository by lazy { MedicationRepositoryImpl(database.medicationDao()) }

    val observeMedications by lazy { ObserveMedicationsUseCase(repository) }
    val addMedication by lazy { AddMedicationUseCase(repository, scheduler, notifier) }
    val updateMedication by lazy { UpdateMedicationUseCase(repository, scheduler) }
    val removeMedication by lazy { RemoveMedicationUseCase(repository, scheduler) }
    val setMedicationTaken by lazy { SetMedicationTakenUseCase(repository) }
    val canScheduleExactAlarms by lazy { CanScheduleExactAlarmsUseCase(scheduler) }
    val rescheduleAll by lazy { RescheduleAllUseCase(repository, scheduler) }
    val triggerReminder by lazy { TriggerReminderUseCase(repository, scheduler, notifier) }
}
