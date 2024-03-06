package com.omar.musica.model.lyrics

/**
 * The source from where we fetched the lyrics of some song
 * This is used mainly to show the user options such as saving the lyrics
 * to the song if it was fetched from the internet or searching for better lyrics
 * on the internet if the lyrics was fetched from the song metadata.
 */
enum class LyricsFetchSource {
    FROM_SONG_METADATA, FROM_INTERNET
}