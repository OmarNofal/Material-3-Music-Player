package com.omar.musica.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omar.musica.database.dao.BlacklistedFoldersDao
import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.database.dao.QueueDao
import com.omar.musica.database.entities.BlacklistedFolderEntity
import com.omar.musica.database.entities.PlaylistEntity
import com.omar.musica.database.entities.PlaylistsSongsEntity
import com.omar.musica.database.entities.QueueEntity


@Database(
    entities = [PlaylistEntity::class, PlaylistsSongsEntity::class, BlacklistedFolderEntity::class, QueueEntity::class],
    version = 2, exportSchema = false
)
abstract class MusicaDatabase : RoomDatabase() {

    abstract fun playlistsDao(): PlaylistDao
    abstract fun blacklistDao(): BlacklistedFoldersDao
    abstract fun queueDao(): QueueDao

}