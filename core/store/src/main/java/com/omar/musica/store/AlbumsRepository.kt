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

    /**
     * Contains simplified information about all albums
     * Used inside the Albums Screen
     */
    val basicAlbums: StateFlow<List<BasicAlbum>> = mediaRepository.songsFlow.map {
        val songs = it.songs

        val albumsNames = songs
            .groupBy { song -> song.metadata.albumName }
            .filter { entry -> entry.key != null }

        val albums = albumsNames.map { entry ->
            val firstSong = entry.value[0]
            BasicAlbum(
                BasicAlbumInfo(
                    entry.key!!,
                    firstSong.metadata.artistName.orEmpty(),
                    entry.value.size
                ),
                firstSong = firstSong
            )
        }

        albums
    }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    fun getArtistAlbums(artistName: String) =
        mediaRepository.songsFlow.map {
            val songs = it.songs
            val artistAlbums = songs
                .filter { it.metadata.artistName == artistName }
                .groupBy { it.metadata.albumName }

            return@map artistAlbums.map {
                val albumInfo = BasicAlbumInfo(it.key!!, artistName, it.value.size)
                BasicAlbum(albumInfo, it.value.firstOrNull())
            }
        }

    fun getAlbumWithSongs(albumName: String) =
        mediaRepository.songsFlow.map {
            val songs = it.songs

            val albumSongs = songs.filter { s -> s.metadata.albumName == albumName }
            if (albumSongs.isEmpty())
                return@map AlbumWithSongs(
                    albumInfo = BasicAlbumInfo(albumName, "", 0),
                    songs = listOf()
                )
            else {
                return@map AlbumWithSongs(
                    albumInfo = BasicAlbumInfo(
                        albumName,
                        albumSongs.first().metadata.artistName ?: "<unknown>",
                        albumSongs.size
                    ),
                    albumSongs.map { s -> AlbumSong(s, 0) } // we will get track number in later update
                )
            }
        }


}