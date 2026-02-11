package com.leolithy.exam_06.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leolithy.exam_06.data.model.MoodEntry
import com.leolithy.exam_06.databinding.ItemMoodBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(private val moods: List<MoodEntry>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]
        holder.binding.apply {
            tvEmoji.text = mood.emoji
            tvMoodDate.text = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(Date(mood.timestamp))
            if (mood.notes.isNotBlank()) {
                tvMoodNotes.text = mood.notes
                tvMoodNotes.visibility = View.VISIBLE
            } else {
                tvMoodNotes.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = moods.size
}