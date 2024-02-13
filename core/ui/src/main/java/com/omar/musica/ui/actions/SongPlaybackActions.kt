package com.omar.musica.ui.actions

import android.content.Context
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.ui.showSongsAddedToNextToast
import com.omar.musica.ui.showSongsAddedToQueueToast
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
    private val context: Context,
    private val playbackManager: PlaybackManager
) : SongPlaybackActions {

    override fun playNext(songs: List<SongUi>) {
        playbackManager.playNext(songs.toSongModels())
        context.showSongsAddedToNextToast(songs.size)
    }

    override fun addToQueue(songs: List<SongUi>) {
        playbackManager.addToQueue(songs.toSongModels())
        context.showSongsAddedToQueueToast(songs.size)
    }

    override fun shuffleNext(songs: List<SongUi>) {
        playbackManager.shuffleNext(songs.toSongModels())
        context.showSongsAddedToNextToast(songs.size)
    }

    override fun shuffle(songs: List<SongUi>) {
        playbackManager.shuffle(songs.toSongModels())
    }

    override fun play(songs: List<SongUi>) {
        playbackManager.setPlaylistAndPlayAtIndex(songs.toSongModels())
    }
}