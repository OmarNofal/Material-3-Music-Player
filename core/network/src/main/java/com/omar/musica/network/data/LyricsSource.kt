package com.omar.musica.network.data

import com.omar.musica.network.model.NetworkErrorException
import com.omar.musica.network.model.NotFoundException
import com.omar.musica.network.model.SongLyricsNetwork
import com.omar.musica.network.service.LyricsService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsSource @Inject constructor(
  private val lyricsService: LyricsService
) {
  suspend fun getSongLyrics(
    artistName: String,
    trackName: String,
    albumName: String,
    durationSeconds: Int,
  ): SongLyricsNetwork {
    return try {
      lyricsService.getSongLyrics(artistName, trackName, albumName, durationSeconds)
    } catch (e: HttpException) {
      if (e.code() == 404) throw NotFoundException("Lyrics not found")
      else throw NetworkErrorException(e.message())
    } catch (e: Exception) {
      throw NetworkErrorException(e.message ?: "Network error")
    }
  }
}