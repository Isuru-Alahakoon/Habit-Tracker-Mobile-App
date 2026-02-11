package com.leolithy.exam_06.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leolithy.exam_06.data.model.Habit
import com.leolithy.exam_06.databinding.ItemHabitBinding

class HabitAdapter(
    private val habits: List<Habit>,
    private val onCheckedChange: (Habit, Boolean) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.binding.apply {
            checkboxHabit.text = habit.name
            checkboxHabit.setOnCheckedChangeListener(null) // Avoid listener firing on bind
            checkboxHabit.isChecked = habit.isCompleted
            checkboxHabit.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(habit, isChecked)
            }
            btnEdit.setOnClickListener { onEditClick(habit) }
            btnDelete.setOnClickListener { onDeleteClick(habit) }
        }
    }

    override fun getItemCount(): Int = habits.size
}