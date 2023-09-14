package com.omar.musica.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.omar.musica.database.entities.PLAYLIST_ID_COLUMN
import com.omar.musica.database.entities.PlaylistEntity
import com.omar.musica.database.entities.PlaylistsSongsEntity


data class PlaylistWithSongsUri(
    @Embedded
    val playlistEntity: PlaylistEntity,

    @Relation(entity = PlaylistsSongsEntity::class, parentColumn = PLAYLIST_ID_COLUMN, entityColumn = PLAYLIST_ID_COLUMN)
    val songUris: List<PlaylistsSongsEntity>
)


//data class PlaylistWithSongsUri(
//    @Embedded
//    val playlistEntity: PlaylistEntity,
//
//    val songs: List<String>,
//)
//
//typealias PlaylistWithSongsUris =
//    Pair<PlaylistEntity, List<String>>