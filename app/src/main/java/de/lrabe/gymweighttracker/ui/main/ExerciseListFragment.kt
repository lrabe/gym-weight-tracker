package de.lrabe.gymweighttracker.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.lrabe.gymweighttracker.MainActivity
import de.lrabe.gymweighttracker.R
import de.lrabe.gymweighttracker.data.Exercise
import de.lrabe.gymweighttracker.util.InjectorUtils
import androidx.recyclerview.widget.DividerItemDecoration


class ExerciseListFragment : Fragment(), ExerciseListAdapter.ClickListener {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = InjectorUtils.provideSharedViewModelFactory(requireActivity().applicationContext)
        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ExerciseListAdapter(view.context, this)
        val layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        viewModel.allExercises.observe(this, Observer { exercises ->
            exercises.let {
                // Update the adapter's cached copy of exercises
                adapter.setExercises(exercises)
            }
        })

        return view
    }


    override fun onItemClick(position: Int, exercise: Exercise, v: View?) {
        viewModel.selectExercise(exercise)
        (activity as MainActivity).setCurrentTab(0)
    }

}
