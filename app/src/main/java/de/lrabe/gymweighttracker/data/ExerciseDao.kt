package de.lrabe.gymweighttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExerciseDao {

    @Query("SELECT * from exercises ORDER BY id ASC")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * from exercises WHERE id = :id")
    fun getExercise(id: String): LiveData<Exercise>

    @Insert
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}