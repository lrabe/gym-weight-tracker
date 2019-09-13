package de.lrabe.gymweighttracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "friendly_name") var friendlyName: String,
    var sets: Int,
    var repetitions: Int,
    var weight: Int
)