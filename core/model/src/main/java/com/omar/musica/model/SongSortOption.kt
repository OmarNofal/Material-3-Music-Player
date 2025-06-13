package com.omar.musica.model

enum class SongSortOption(val title: String) {
    TITLE("Title"), ALBUM("Album"), ARTIST("Artist"), FileSize("File size"), Duration("Duration")
}

enum class AlbumsSortOption {
    NAME, ARTIST, NUMBER_OF_SONGS
}