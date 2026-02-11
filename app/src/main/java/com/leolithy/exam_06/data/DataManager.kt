package com.leolithy.exam_06.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leolithy.exam_06.data.model.Habit
import com.leolithy.exam_06.data.model.MoodEntry

object DataManager {

    private const val PREFS_NAME = "ZenithWellnessPrefs"
    private const val KEY_HABITS = "habits"
    private const val KEY_MOODS = "moods"
    private const val KEY_HYDRATION_INTERVAL = "hydration_interval"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    fun loadHabits(): MutableList<Habit> {
        val json = prefs.getString(KEY_HABITS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveMoods(moods: List<MoodEntry>) {
        val json = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, json).apply()
    }

    fun loadMoods(): MutableList<MoodEntry> {
        val json = prefs.getString(KEY_MOODS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveHydrationInterval(minutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, minutes).apply()
    }

    fun loadHydrationInterval(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL, 60) // Default to 60 minutes
    }
}