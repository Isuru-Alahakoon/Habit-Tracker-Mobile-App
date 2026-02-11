package com.leolithy.exam_06.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leolithy.exam_06.R
import com.leolithy.exam_06.data.DataManager
import com.leolithy.exam_06.data.model.Habit
import com.leolithy.exam_06.databinding.DialogAddEditHabitBinding
import com.leolithy.exam_06.databinding.FragmentHabitsBinding

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitAdapter: HabitAdapter
    private val habitsList = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadHabits()
        updateProgress()
        binding.fabAddHabit.setOnClickListener {
            showAddEditHabitDialog(null)
        }
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habitsList,
            onCheckedChange = { habit, isChecked ->
                habit.isCompleted = isChecked
                DataManager.saveHabits(habitsList)
                updateProgress()
            },
            onEditClick = { habit -> showAddEditHabitDialog(habit) },
            onDeleteClick = { habit -> showDeleteConfirmationDialog(habit) }
        )
        binding.recyclerViewHabits.adapter = habitAdapter
    }

    private fun loadHabits() {
        habitsList.clear()
        habitsList.addAll(DataManager.loadHabits())
        habitAdapter.notifyDataSetChanged()
    }

    private fun updateProgress() {
        val completedCount = habitsList.count { it.isCompleted }
        val totalCount = habitsList.size
        val progress = if (totalCount > 0) (completedCount * 100 / totalCount) else 0

        binding.progressBar.progress = progress
        binding.tvProgressPercentage.text = getString(R.string.habits_progress, progress)
    }

    private fun showAddEditHabitDialog(habit: Habit?) {
        val dialogBinding = DialogAddEditHabitBinding.inflate(LayoutInflater.from(context))
        val isEditing = habit != null

        dialogBinding.dialogTitle.text = if (isEditing) getString(R.string.edit_habit) else getString(R.string.add_habit)
        if (isEditing) {
            dialogBinding.etHabitName.setText(habit?.name)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val habitName = dialogBinding.etHabitName.text.toString().trim()
                if (habitName.isNotEmpty()) {
                    if (isEditing) {
                        habit?.name = habitName
                        DataManager.saveHabits(habitsList)
                        habitAdapter.notifyDataSetChanged()
                    } else {
                        val newHabit = Habit(name = habitName)
                        habitsList.add(newHabit)
                        DataManager.saveHabits(habitsList)
                        habitAdapter.notifyItemInserted(habitsList.size - 1)
                    }
                    updateProgress()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_habit)
            .setMessage(R.string.delete_habit_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                val position = habitsList.indexOf(habit)
                if (position != -1) {
                    habitsList.removeAt(position)
                    DataManager.saveHabits(habitsList)
                    habitAdapter.notifyItemRemoved(position)
                    updateProgress()
                }
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}