package de.lrabe.gymweighttracker.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ExerciseRepository private constructor(private val exerciseDao: ExerciseDao) {

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()

    @WorkerThread
    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }

    fun get(id: String): LiveData<Exercise> {
        return exerciseDao.getExercise(id)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    companion object {

        @Volatile private var instance: ExerciseRepository? = null

        fun getInstance(exerciseDao: ExerciseDao) =
            instance ?: synchronized(this) {
                instance ?: ExerciseRepository(exerciseDao).also { instance = it }
            }
    }
}