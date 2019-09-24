package de.lrabe.gymweighttracker.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.lrabe.gymweighttracker.R
import de.lrabe.gymweighttracker.data.Exercise
import de.lrabe.gymweighttracker.ui.main.SharedViewModel.NfcStatus.*
import de.lrabe.gymweighttracker.util.InjectorUtils
import de.lrabe.gymweighttracker.util.hideKeyboard
import java.util.*
import android.widget.EditText


class MainFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory =
            InjectorUtils.provideSharedViewModelFactory(requireActivity().applicationContext)
        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val placeholderContainer = view.findViewById<ConstraintLayout>(R.id.placeholder_container)
        val exerciseContainer = view.findViewById<ConstraintLayout>(R.id.exercise_container)

        val nfcImage = view.findViewById<ImageView>(R.id.nfc_image)
        val nfcStatus = view.findViewById<TextView>(R.id.nfc_status)

        val exerciseFriendlyName = view.findViewById<EditText>(R.id.exercise_friendly_name)
        val exerciseId = view.findViewById<TextView>(R.id.exercise_id)
        val exerciseSets = view.findViewById<EditText>(R.id.exercise_sets)
        val exerciseRepetitions = view.findViewById<EditText>(R.id.exercise_repetitions)
        val exerciseWeight = view.findViewById<EditText>(R.id.exercise_weight)

        val closeButton = view.findViewById<Button>(R.id.button_close)
        val deleteButton = view.findViewById<Button>(R.id.button_delete)
        val addButton = view.findViewById<FloatingActionButton>(R.id.button_add)

        val onFocusChangeListener = View.OnFocusChangeListener { focusedView, hasFocus ->
            if (!hasFocus) {
                focusedView.hideKeyboard()
            }
        }
        exerciseFriendlyName.onFocusChangeListener = onFocusChangeListener
        exerciseSets.onFocusChangeListener = onFocusChangeListener
        exerciseRepetitions.onFocusChangeListener = onFocusChangeListener
        exerciseWeight.onFocusChangeListener = onFocusChangeListener

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val currentExercise = viewModel.selectedExercise.value
                currentExercise?.apply {
                    // adapted from https://stackoverflow.com/a/13787221
                    when (editable.hashCode()) {
                        exerciseFriendlyName.text.hashCode() ->
                            friendlyName = exerciseFriendlyName.text.toString()
                        exerciseSets.text.hashCode() ->
                            sets = exerciseSets.text.toString().toIntOrNull() ?: 0
                        exerciseRepetitions.text.hashCode() ->
                            repetitions = exerciseRepetitions.text.toString().toIntOrNull() ?: 0
                        exerciseWeight.text.hashCode() ->
                            weight = exerciseWeight.text.toString().toIntOrNull() ?: 0
                    }
                }
                currentExercise?.let {
                    viewModel.update(it)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
        exerciseFriendlyName.addTextChangedListener(textWatcher)
        exerciseSets.addTextChangedListener(textWatcher)
        exerciseRepetitions.addTextChangedListener(textWatcher)
        exerciseWeight.addTextChangedListener(textWatcher)


        closeButton.setOnClickListener {
            viewModel.selectExercise(null)
        }

        deleteButton.setOnClickListener {
            viewModel.selectedExercise.value?.let { exercise -> viewModel.delete(exercise) }
            viewModel.selectExercise(null)
        }

        addButton.setOnClickListener {
            val newExercise = Exercise(UUID.randomUUID().toString(), "", 0, 0, 0)
            viewModel.insert(newExercise)
            viewModel.selectExercise(newExercise)
        }

        viewModel.selectedExercise.observe(this, Observer { selectedExercise ->
            if (selectedExercise == null) {
                placeholderContainer.visibility = View.VISIBLE
                exerciseContainer.visibility = View.GONE
            } else {
                placeholderContainer.visibility = View.GONE
                exerciseContainer.visibility = View.VISIBLE

                exerciseFriendlyName.setText(selectedExercise.friendlyName)
                exerciseId.text = selectedExercise.id
                exerciseSets.setText(selectedExercise.sets.toString())
                exerciseRepetitions.setText(selectedExercise.repetitions.toString())
                exerciseWeight.setText(selectedExercise.weight.toString())
            }
        })

        viewModel.nfcStatus.observe(this, Observer { status ->
            when (status) {
                MISSING -> {
                    nfcImage.visibility = View.GONE
                    nfcStatus.visibility = View.GONE
                }
                DISABLED -> {
                    nfcImage.visibility = View.VISIBLE
                    nfcStatus.visibility = View.VISIBLE
                    nfcStatus.text = getText(R.string.nfc_status_disabled)
                }
                ENABLED -> {
                    nfcImage.visibility = View.VISIBLE
                    nfcStatus.visibility = View.VISIBLE
                    nfcStatus.text = getText(R.string.nfc_status_enabledy)
                }
                else -> {
                }
            }
        })

        return view
    }
}
