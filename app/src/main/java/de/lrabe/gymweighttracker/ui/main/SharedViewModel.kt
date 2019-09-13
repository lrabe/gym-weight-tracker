package de.lrabe.gymweighttracker.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.lrabe.gymweighttracker.data.Exercise
import de.lrabe.gymweighttracker.data.ExerciseRepository
import kotlinx.coroutines.launch

class SharedViewModel internal constructor(private val repository: ExerciseRepository) :
    ViewModel() {

    fun selectExercise(exercise: Exercise?) {
        selectedExercise.value = exercise
    }

    val selectedExercise = MutableLiveData<Exercise?>(null)

    fun setNfcStatus(status: NfcStatus) {
        nfcStatus.value = status
    }

    val nfcStatus = MutableLiveData<NfcStatus>(NfcStatus.MISSING)

    val allExercises = repository.allExercises

    fun insert(exercise: Exercise) = viewModelScope.launch {
        repository.insert(exercise)
    }

    fun update(exercise: Exercise) = viewModelScope.launch {
        repository.update(exercise)
    }

    fun delete(exercise: Exercise) = viewModelScope.launch {
        repository.delete(exercise)
    }

    fun get(id: String) =
        repository.get(id)

    enum class NfcStatus {
        MISSING, ENABLED, DISABLED
    }
}
