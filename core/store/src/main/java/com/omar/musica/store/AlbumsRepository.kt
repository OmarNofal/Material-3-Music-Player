package com.omar.musica.store

import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.store.model.album.AlbumWithSongs
import com.omar.musica.store.model.album.BasicAlbum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class AlbumsRepository @Inject constructor(
    val mediaRepository: MediaRepository
) {

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * All the albums of the device alongside their songs
     */
    val albums: StateFlow<List<AlbumWithSongs>> = mediaRepository.songsFlow
        .map {

            val songs = it.songs

            val albumsNames = songs
                .groupBy { song -> song.metadata.albumName }
                .filter { entry -> entry.key != null }

            var counter = 1
            albumsNames.map { entry ->
                val firstSong = entry.value[0]
                AlbumWithSongs(
                    BasicAlbumInfo(
                        counter++,
                        entry.key!!,
                        firstSong.metadata.artistName.orEmpty(),
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
     * Contains simplified information about all albums
     * Used inside the Albums Screen
     */
    val basicAlbums: StateFlow<List<BasicAlbum>> = albums
        .map { albums -> albums.map { BasicAlbum(it.albumInfo, it.songs.firstOrNull()?.song) } }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            listOf()
        )

    fun getArtistAlbums(artistName: String) =
        basicAlbums.map { it.filter { album -> album.albumInfo.artist == artistName } }

    fun getAlbumWithSongs(albumId: Int) =
        albums.map { allAlbums ->
            allAlbums
                .firstOrNull { it.albumInfo.id == albumId }
                .let {
                    if (it == null) return@let it
                    // sort the songs by track number
                    val sortedSongs = it.songs.sortedBy { song -> song.trackNumber }
                    it.copy(songs = sortedSongs)
                }
        }

}