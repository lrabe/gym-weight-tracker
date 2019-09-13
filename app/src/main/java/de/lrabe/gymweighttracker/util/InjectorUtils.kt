package de.lrabe.gymweighttracker.util

import android.content.Context
import de.lrabe.gymweighttracker.data.AppDatabase
import de.lrabe.gymweighttracker.data.ExerciseRepository
import de.lrabe.gymweighttracker.ui.main.SharedViewModelFactory

object InjectorUtils {
    fun provideSharedViewModelFactory(
        context: Context
    ): SharedViewModelFactory {
        val repository = ExerciseRepository.getInstance(
            AppDatabase.getDatabase(context.applicationContext).exerciseDao()
        )
        return SharedViewModelFactory(repository)
    }
}