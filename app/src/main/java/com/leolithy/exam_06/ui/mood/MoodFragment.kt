package com.leolithy.exam_06.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leolithy.exam_06.R
import com.leolithy.exam_06.data.DataManager
import com.leolithy.exam_06.data.model.MoodEntry
import com.leolithy.exam_06.databinding.DialogLogMoodBinding
import com.leolithy.exam_06.databinding.FragmentMoodBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var moodAdapter: MoodAdapter
    private val moodList = mutableListOf<MoodEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadMoods()
        setupMoodChart()
        binding.fabAddMood.setOnClickListener {
            showLogMoodDialog()
        }
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moodList)
        binding.recyclerViewMoods.apply {
            layoutManager = LinearLayoutManager(context).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            adapter = moodAdapter
        }
    }

    private fun loadMoods() {
        moodList.clear()
        moodList.addAll(DataManager.loadMoods())
        moodAdapter.notifyDataSetChanged()
    }

    private fun showLogMoodDialog() {
        val dialogBinding = DialogLogMoodBinding.inflate(LayoutInflater.from(context))
        var selectedEmoji = ""

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        val selectEmoji = { emoji: String, view: View ->
            selectedEmoji = emoji
            dialogBinding.emojiHappy.alpha = if (emoji == getString(R.string.great_emoji)) 1.0f else 0.4f
            dialogBinding.emojiNeutral.alpha = if (emoji == getString(R.string.okay_emoji)) 1.0f else 0.4f
            dialogBinding.emojiSad.alpha = if (emoji == getString(R.string.sad_emoji)) 1.0f else 0.4f
        }

        dialogBinding.emojiHappy.setOnClickListener { selectEmoji(getString(R.string.great_emoji), it) }
        dialogBinding.emojiNeutral.setOnClickListener { selectEmoji(getString(R.string.okay_emoji), it) }
        dialogBinding.emojiSad.setOnClickListener { selectEmoji(getString(R.string.sad_emoji), it) }

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                if (selectedEmoji.isEmpty()) {
                    Toast.makeText(context, "Please select a mood emoji", Toast.LENGTH_SHORT).show()
                } else {
                    val notes = dialogBinding.etMoodNotes.text.toString().trim()
                    val newMood = MoodEntry(emoji = selectedEmoji, notes = notes)
                    moodList.add(newMood)
                    DataManager.saveMoods(moodList)
                    moodAdapter.notifyItemInserted(moodList.size - 1)
                    binding.recyclerViewMoods.scrollToPosition(moodList.size - 1)
                    setupMoodChart() // Refresh chart
                    Toast.makeText(context, R.string.mood_logged_success, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun setupMoodChart() {
        val moodsLast7Days = moodList.filter {
            System.currentTimeMillis() - it.timestamp <= TimeUnit.DAYS.toMillis(7)
        }

        if (moodsLast7Days.isEmpty()) {
            binding.moodChart.clear()
            binding.moodChart.data = null
            binding.moodChart.invalidate()
            return
        }

        val calendar = Calendar.getInstance()
        val dailyAverageMoods = moodsLast7Days
            .groupBy {
                calendar.timeInMillis = it.timestamp
                calendar.get(Calendar.DAY_OF_YEAR)
            }
            .mapValues { entry ->
                entry.value.map { getMoodValue(it.emoji) }.average()
            }

        val entries = dailyAverageMoods.map { (dayOfYear, avgMood) ->
            Entry(dayOfYear.toFloat(), avgMood.toFloat())
        }.sortedBy { it.x }

        val dataSet = LineDataSet(entries, "Mood Level").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = ContextCompat.getColor(requireContext(), R.color.zen_green_primary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.zen_text_secondary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.zen_green_primary))
            lineWidth = 2.5f
            circleRadius = 5f
            setDrawCircleHole(true)
            circleHoleColor = ContextCompat.getColor(requireContext(), R.color.zen_surface)
            circleHoleRadius = 2.5f
            valueTextSize = 10f
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        }

        binding.moodChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.zen_text_secondary)
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                private val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                override fun getFormattedValue(value: Float): String {
                    calendar.set(Calendar.DAY_OF_YEAR, value.toInt())
                    return sdf.format(calendar.time)
                }
            }
            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.zen_text_secondary)
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = ContextCompat.getColor(requireContext(), R.color.zen_background)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 6f
            axisLeft.setLabelCount(4, true)
            axisRight.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            invalidate()
        }
    }

    private fun getMoodValue(emoji: String): Int {
        return when (emoji) {
            getString(R.string.great_emoji) -> 5
            getString(R.string.okay_emoji) -> 3
            getString(R.string.sad_emoji) -> 1
            else -> 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}