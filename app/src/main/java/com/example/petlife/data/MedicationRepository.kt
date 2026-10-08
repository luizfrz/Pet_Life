package com.example.petlife.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class MedicationRepository(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<Medication> {
        val array = JSONArray(prefs.getString(KEY_ITEMS, "[]"))
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            Medication(
                id = o.getInt("id"),
                petName = o.getString("pet"),
                medicationName = o.getString("medication"),
                hour = o.getInt("hour"),
                minute = o.getInt("minute")
            )
        }
    }

    fun get(id: Int): Medication? = getAll().firstOrNull { it.id == id }

    fun add(petName: String, medicationName: String, hour: Int, minute: Int): Medication {
        val id = prefs.getInt(KEY_NEXT_ID, 1)
        val medication = Medication(id, petName, medicationName, hour, minute)
        save(getAll() + medication)
        prefs.edit().putInt(KEY_NEXT_ID, id + 1).apply()
        return medication
    }

    fun remove(id: Int) = save(getAll().filterNot { it.id == id })

    private fun save(items: List<Medication>) {
        val array = JSONArray()
        items.forEach {
            array.put(
                JSONObject()
                    .put("id", it.id)
                    .put("pet", it.petName)
                    .put("medication", it.medicationName)
                    .put("hour", it.hour)
                    .put("minute", it.minute)
            )
        }
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply()
    }

    private companion object {
        const val PREFS_NAME = "medications"
        const val KEY_ITEMS = "items"
        const val KEY_NEXT_ID = "next_id"
    }
}
