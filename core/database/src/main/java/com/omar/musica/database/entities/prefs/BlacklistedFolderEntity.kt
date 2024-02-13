package com.omar.musica.database.entities.prefs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


internal const val FOLDER_PATH_COLUMN = "path"

@Entity(tableName = "black_list")
data class BlacklistedFolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = FOLDER_PATH_COLUMN)
    val folderPath: String
)