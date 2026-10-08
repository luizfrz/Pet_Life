package com.example.petlife.domain.usecase

import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.notification.MedicationNotifier
import com.example.petlife.domain.repository.MedicationRepository
import com.example.petlife.domain.scheduler.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddMedicationUseCaseTest {
    private val repository = FakeRepository()
    private val scheduler = FakeScheduler()
    private val notifier = FakeNotifier()
    private val useCase = AddMedicationUseCase(repository, scheduler, notifier)

    @Test
    fun `saves, schedules and notifies when input is valid`() = runBlocking {
        val result = useCase("Rex", " Dipirona ", 8, 30)

        val saved = result.getOrThrow()
        assertEquals("Dipirona", saved.medicationName)
        assertEquals(listOf(saved), repository.items)
        assertEquals(listOf(saved), scheduler.scheduled)
        assertEquals(1, notifier.scheduledCount)
    }

    @Test
    fun `rejects blank medication name without side effects`() = runBlocking {
        val result = useCase("Rex", "   ", 8, 0)

        assertTrue(result.isFailure)
        assertTrue(repository.items.isEmpty())
        assertTrue(scheduler.scheduled.isEmpty())
        assertEquals(0, notifier.scheduledCount)
    }

    @Test
    fun `rejects invalid time`() = runBlocking {
        assertTrue(useCase("Rex", "Dipirona", 24, 0).isFailure)
        assertTrue(useCase("Rex", "Dipirona", 8, 60).isFailure)
        assertTrue(repository.items.isEmpty())
    }

    @Test
    fun `remove cancels the alarm and deletes the medication`() = runBlocking {
        val saved = useCase("", "Dipirona", 8, 0).getOrThrow()

        RemoveMedicationUseCase(repository, scheduler)(saved.id)

        assertTrue(repository.items.isEmpty())
        assertEquals(listOf(saved.id), scheduler.cancelled)
    }

    @Test
    fun `reminder trigger unchecks taken, notifies and reschedules`() = runBlocking {
        val saved = useCase("", "Dipirona", 8, 0).getOrThrow()
        SetMedicationTakenUseCase(repository)(saved.id, true)
        assertTrue(repository.items.single().taken)
        scheduler.scheduled.clear()

        TriggerReminderUseCase(repository, scheduler, notifier)(saved.id)

        assertEquals(false, repository.items.single().taken)
        assertEquals(1, scheduler.scheduled.size)
    }

    @Test
    fun `update changes fields, unchecks taken and reschedules the same id`() = runBlocking {
        val saved = useCase("Rex", "Dipirona", 8, 0).getOrThrow()
        SetMedicationTakenUseCase(repository)(saved.id, true)
        scheduler.scheduled.clear()

        val updated = UpdateMedicationUseCase(repository, scheduler)(saved.id, "Rex", " Amoxicilina ", 21, 15).getOrThrow()

        assertEquals(saved.id, updated.id)
        assertEquals("Amoxicilina", repository.items.single().medicationName)
        assertEquals("21:15", repository.items.single().timeLabel)
        assertEquals(false, repository.items.single().taken)
        assertEquals(listOf(updated), scheduler.scheduled)
    }

    @Test
    fun `update rejects blank name and unknown id`() = runBlocking {
        val update = UpdateMedicationUseCase(repository, scheduler)
        val saved = useCase("", "Dipirona", 8, 0).getOrThrow()

        assertTrue(update(saved.id, "", " ", 8, 0).isFailure)
        assertTrue(update(999, "", "X", 8, 0).isFailure)
        assertEquals("Dipirona", repository.items.single().medicationName)
    }

    private class FakeRepository : MedicationRepository {
        val items = mutableListOf<Medication>()
        private val flow = MutableStateFlow<List<Medication>>(emptyList())

        override fun observeAll(): Flow<List<Medication>> = flow
        override suspend fun getAll() = items.toList()
        override suspend fun get(id: Long) = items.firstOrNull { it.id == id }
        override suspend fun add(medication: Medication): Medication =
            medication.copy(id = items.size + 1L).also { items.add(it); flow.value = items.toList() }

        override suspend fun update(medication: Medication) {
            val index = items.indexOfFirst { it.id == medication.id }
            if (index >= 0) items[index] = medication
            flow.value = items.toList()
        }

        override suspend fun setTaken(id: Long, taken: Boolean) {
            val index = items.indexOfFirst { it.id == id }
            if (index >= 0) items[index] = items[index].copy(taken = taken)
            flow.value = items.toList()
        }

        override suspend fun remove(id: Long) {
            items.removeAll { it.id == id }
            flow.value = items.toList()
        }
    }

    private class FakeScheduler : ReminderScheduler {
        val scheduled = mutableListOf<Medication>()
        val cancelled = mutableListOf<Long>()
        override fun canScheduleExact() = true
        override fun schedule(medication: Medication) { scheduled.add(medication) }
        override fun cancel(medicationId: Long) { cancelled.add(medicationId) }
    }

    private class FakeNotifier : MedicationNotifier {
        var scheduledCount = 0
        override fun showScheduled() { scheduledCount++ }
        override fun showReminder(medication: Medication) = Unit
    }
}
