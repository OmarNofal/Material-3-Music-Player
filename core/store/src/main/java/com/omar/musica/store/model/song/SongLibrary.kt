package com.omar.musica.store.model.song


class SongLibrary(
    val songs: List<Song>
) {

    /**
     * Map of song Uri to their Uris
     */
    private val songMap: Map<String, Song> = kotlin.run {
        val map = mutableMapOf<String, Song>()
        songs.forEach { song ->
            map[song.uri.toString()] = song
        }
        map
    }

    fun getSongByUri(uri: String): Song? = songMap[uri]

    fun getSongsByUris(uris: List<String>): List<Song> = kotlin.run {
        val result = mutableListOf<Song>()
        uris.forEach {
            val song = songMap[it] ?: return@forEach
            result.add(song)
        }
        result
    }

}