package com.omar.musica.store

import com.omar.musica.model.album.BasicAlbumInfo
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
    val basicAlbums: StateFlow<List<BasicAlbum>>
     = mediaRepository.songsFlow.
            map {
                val songs = it.songs

                val albumsNames = songs
                    .groupBy { song -> song.metadata.albumName }
                    .filter { entry -> entry.key != null }

                val albums = albumsNames.map { entry ->
                    val firstSong = entry.value[0]
                    BasicAlbum(
                        BasicAlbumInfo(entry.key!!, firstSong.metadata.artistName.orEmpty(), entry.value.size),
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




}