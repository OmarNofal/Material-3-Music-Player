package com.omar.musica.ui.actions

import com.omar.musica.playback.PlaybackManager
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toSongModels

interface SongPlaybackActions {

    fun playNext(songs: List<SongUi>)
    fun addToQueue(songs: List<SongUi>)
    fun shuffleNext(songs: List<SongUi>)
    fun shuffle(songs: List<SongUi>)
    fun play(songs: List<SongUi>)

}


class SongPlaybackActionsImpl(
    private val playbackManager: PlaybackManager
) : SongPlaybackActions {

    override fun playNext(songs: List<SongUi>) {
        playbackManager.playNext(songs.toSongModels())
    }

    override fun addToQueue(songs: List<SongUi>) {
        playbackManager.addToQueue(songs.toSongModels())
    }

    override fun shuffleNext(songs: List<SongUi>) {
        playbackManager.shuffleNext(songs.toSongModels())
    }

    override fun shuffle(songs: List<SongUi>) {
        playbackManager.shuffle(songs.toSongModels())
    }

    override fun play(songs: List<SongUi>) {
        playbackManager.setPlaylistAndPlayAtIndex(songs.toSongModels())
    }
}