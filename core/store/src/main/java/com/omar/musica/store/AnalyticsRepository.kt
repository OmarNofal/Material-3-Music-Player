package com.omar.musica.store

import com.omar.musica.database.dao.ActivityDao
import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.database.entities.activity.ListeningSessionEntity
import com.omar.musica.model.activity.ListeningSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
  private val activityDao: ActivityDao,
) {
  private val scope = CoroutineScope(Dispatchers.IO)

  fun insertListeningSession(l: ListeningSession) {
    scope.launch {
      activityDao.insertListeningSession(l.toDBEntity())
    }
  }

  suspend fun getAllListeningSessions(): List<ListeningSession> {
    return activityDao.getAllListeningSessions().map { it.toModel() }
  }

  private fun ListeningSession.toDBEntity() =
    ListeningSessionEntity(0, startTime.time, durationSeconds)

  private fun ListeningSessionEntity.toModel() =
    ListeningSession(Date(startTimeEpoch), durationSeconds)

  private val Date.timeSeconds get() = time / 1000

}