package com.omar.musica.store

import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.database.entities.PlaylistEntity
import com.omar.musica.database.model.PlaylistInfoWithNumberOfSongs
import com.omar.musica.model.Playlist
import com.omar.musica.model.PlaylistInfo
import com.omar.musica.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistsRepository @Inject constructor(
    private val playlistsDao: PlaylistDao,
    private val mediaRepository: MediaRepository,
) {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val playlistsWithInfoFlows =
        playlistsDao.getPlaylistsInfoFlow()
            .map {
                it.toDomainPlaylists()
            }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), listOf())


    fun createPlaylist(name: String) {
        coroutineScope.launch {
            val playlist = PlaylistEntity(name = name)
            playlistsDao.createPlaylist(playlist)
        }
    }

    fun addSongsToPlaylists(songsUris: List<String>, playlists: List<PlaylistInfo>) {
        coroutineScope.launch {
            playlistsDao.insertSongsToPlaylists(songsUris, playlists.toDBEntities())
        }
    }

    fun getPlaylistWithSongsFlow(playlistId: Int): Flow<Playlist> =
        combine(
            mediaRepository.songsFlow,
            playlistsDao.getPlaylistWithSongsFlow(playlistId)
        ) { library, playlistWithSongs ->

            // Convert the songs to a map to enable fast retrieval
            val songsSet = library.songs.associateBy { it.uriString }

            // The uris of the song
            val playlistSongsUriStrings = playlistWithSongs.songUris

            val playlistSongs = mutableListOf<Song>()
            for (uriString in playlistSongsUriStrings.map { it.songUriString }) {
                val song = songsSet[uriString]
                if (song != null) {
                    playlistSongs.add(song)
                }
            }

            val playlistInfo = playlistWithSongs.playlistEntity
            Playlist(
                PlaylistInfo(playlistInfo.id, playlistInfo.name ?: "", playlistSongs.size),
                playlistSongs
            )
        }


    private fun PlaylistInfo.toDBEntity() =
        PlaylistEntity(id, name)

    private fun List<PlaylistInfo>.toDBEntities() =
        map { it.toDBEntity() }

    private fun PlaylistInfoWithNumberOfSongs.toDomainPlaylist() =
        PlaylistInfo(
            id = playlistEntity.id,
            name = playlistEntity.name ?: "",
            numberOfSongs = numberOfSongs
        )

    private fun List<PlaylistInfoWithNumberOfSongs>.toDomainPlaylists() =
        map { it.toDomainPlaylist() }


}