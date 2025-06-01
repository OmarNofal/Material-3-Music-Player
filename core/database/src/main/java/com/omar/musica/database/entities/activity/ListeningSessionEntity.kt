package com.omar.musica.database.entities.activity

import android.view.inspector.InspectionCompanion
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omar.musica.database.entities.DURATION_SECONDS_COLUMN
import com.omar.musica.database.entities.LISTENING_SESSION_TABLE
import com.omar.musica.database.entities.START_TIME_COLUMN


@Entity(LISTENING_SESSION_TABLE)
data class ListeningSessionEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = START_TIME_COLUMN)
    val startTimeEpoch: Long, // convert to kotlin datetime,
    @ColumnInfo(name = DURATION_SECONDS_COLUMN)
    val durationSeconds: Int,
)