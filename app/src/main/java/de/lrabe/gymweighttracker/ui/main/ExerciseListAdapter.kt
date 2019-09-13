package de.lrabe.gymweighttracker.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lrabe.gymweighttracker.R
import de.lrabe.gymweighttracker.data.Exercise


class ExerciseListAdapter internal constructor(
    context: Context,
    private val onClickListener: ClickListener
) : RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises = emptyList<Exercise>() // Cached copy of exercises

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            onClickListener.onItemClick(adapterPosition, exercises[adapterPosition], v)
        }

        val exerciseItemView: TextView = itemView.findViewById(R.id.list_item_friendly_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.list_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val current = exercises[position]
        holder.exerciseItemView.text = current.friendlyName
    }

    internal fun setExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

    override fun getItemCount() = exercises.size

    interface ClickListener {
        fun onItemClick(position: Int, exercise: Exercise, v: View?)
    }
}