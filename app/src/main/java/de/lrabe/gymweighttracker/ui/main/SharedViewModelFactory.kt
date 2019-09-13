package de.lrabe.gymweighttracker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.lrabe.gymweighttracker.data.ExerciseRepository

class SharedViewModelFactory(
    private val repository: ExerciseRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SharedViewModel(repository) as T
    }
}