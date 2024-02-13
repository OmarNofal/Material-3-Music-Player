package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.omar.musica.database.entities.LISTENING_SESSION_TABLE
import com.omar.musica.database.entities.START_TIME_COLUMN
import com.omar.musica.database.entities.activity.ListeningSessionEntity


@Dao
interface ActivityDao {

    @Insert
    fun insertListeningSession(l: ListeningSessionEntity)

    @Query("SELECT * FROM $LISTENING_SESSION_TABLE")
    fun getAllListeningSessions(): List<ListeningSessionEntity>

    @Query("SELECT * FROM $LISTENING_SESSION_TABLE WHERE $START_TIME_COLUMN > :timestamp")
    fun getListeningSessionsAfterTimestamp(timestamp: Long): List<ListeningSessionEntity>

    @Query(
        "SELECT * FROM $LISTENING_SESSION_TABLE WHERE $START_TIME_COLUMN > :timestamp1"
        + " AND $START_TIME_COLUMN < :timestamp2"
    )
    fun getListeningSessionsBetween(timestamp1: Long, timestamp2: Long): List<ListeningSessionEntity>

    @Query(
        "DELETE FROM $LISTENING_SESSION_TABLE WHERE $START_TIME_COLUMN < :timestamp"
    )
    fun deleteSessionsBeforeTimestamp(timestamp: Long)

    @Query("DELETE FROM $LISTENING_SESSION_TABLE")
    fun deleteAllSessions()

    @Delete
    fun deleteListeningSession(l: ListeningSessionEntity)

    @Query("DELETE FROM $LISTENING_SESSION_TABLE WHERE id = :id")
    fun deleteListeningSessionById(id: Long)
}