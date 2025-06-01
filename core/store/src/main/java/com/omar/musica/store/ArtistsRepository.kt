package com.omar.musica.store

import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.store.model.album.AlbumWithSongs
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.store.model.song.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class ArtistsRepository @Inject constructor(
  val mediaRepository: MediaRepository
) {

  private val scope = CoroutineScope(Dispatchers.Default)

  /**
   * All the artists of the device alongside their songs
   */
  val artists: StateFlow<List<AlbumWithSongs>> = mediaRepository.songsFlow
    .map {

      val songs = it.songs

      // 关键改动：按歌手名分组而不是专辑名
      val artistsNames = songs.groupBy { song -> song.metadata.artistName }.filter { entry -> entry.key != null }

      var counter = 1
      artistsNames.map { entry ->
        val firstSong = entry.value[0]
        AlbumWithSongs(
          BasicAlbumInfo(
            counter++,
            entry.key!!, // 歌手名
            "", // artist字段留空，因为这里name就是歌手名
            entry.value.size
          ),
          entry.value.map { AlbumSong(it, it.metadata.trackNumber) }
        )
      }
    }
    .stateIn(
      scope,
      SharingStarted.Eagerly,
      listOf()
    )

  /**
   * Contains simplified information about all artists
   * Used inside the Artists Screen
   */
  val basicArtists: StateFlow<List<BasicAlbum>> = artists
    .map { artists -> artists.map { BasicAlbum(it.albumInfo, it.songs.firstOrNull()?.song) } }
    .stateIn(
      scope,
      SharingStarted.Eagerly,
      listOf()
    )

  fun getArtistAlbums(artistName: String) =
    basicArtists.map { it.filter { artist -> artist.albumInfo.name == artistName } }

  fun getArtistWithSongs(artistId: Int) =
    artists.map { allArtists ->
      allArtists
        .firstOrNull { it.albumInfo.id == artistId }
        .let {
          if (it == null) return@let it
          // sort the songs by track number or title
          val sortedSongs = it.songs.sortedBy { song -> song.song.metadata.title }
          it.copy(songs = sortedSongs)
        }
    }
}