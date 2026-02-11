package com.leolithy.exam_06.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.leolithy.exam_06.R
import com.leolithy.exam_06.data.DataManager
import com.leolithy.exam_06.databinding.FragmentSettingsBinding
import com.leolithy.exam_06.utils.HydrationReminderReceiver
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()

        binding.btnSetReminder.setOnClickListener {
            val intervalStr = binding.etReminderInterval.text.toString()
            if (intervalStr.isNotEmpty() && intervalStr.toInt() > 0) {
                val intervalMinutes = intervalStr.toInt()
                DataManager.saveHydrationInterval(intervalMinutes)
                setHydrationReminder(intervalMinutes)
            } else {
                Toast.makeText(context, "Please enter a valid interval.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShareSummary.setOnClickListener {
            shareMoodSummary()
        }
    }

    private fun loadSettings() {
        val interval = DataManager.loadHydrationInterval()
        binding.etReminderInterval.setText(interval.toString())
    }

    private fun setHydrationReminder(intervalMinutes: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val intervalMillis = intervalMinutes * 60 * 1000L
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis,
            intervalMillis, pendingIntent
        )
        Toast.makeText(context, getString(R.string.reminder_set_toast, intervalMinutes), Toast.LENGTH_SHORT).show()
    }

    private fun shareMoodSummary() {
        val moods = DataManager.loadMoods()
        if (moods.isEmpty()) {
            Toast.makeText(context, R.string.no_moods_to_share, Toast.LENGTH_SHORT).show()
            return
        }
        val summary = StringBuilder("My Recent Moods:\n\n")
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        moods.takeLast(7).reversed().forEach {
            summary.append("${it.emoji} on ${dateFormat.format(Date(it.timestamp))}")
            if (it.notes.isNotEmpty()) {
                summary.append(": \"${it.notes}\"\n")
            } else {
                summary.append("\n")
            }
        }
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, summary.toString())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.mood_summary_title)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}