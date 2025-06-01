package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.omar.musica.database.entities.prefs.BlacklistedFolderEntity
import com.omar.musica.database.entities.prefs.FOLDER_PATH_COLUMN
import kotlinx.coroutines.flow.Flow


@Dao
interface BlacklistedFoldersDao {

  @Query("SELECT * FROM black_list")
  fun getAllBlacklistedFoldersFlow(): Flow<List<BlacklistedFolderEntity>>

  @Insert
  suspend fun addFolder(folder: BlacklistedFolderEntity)

  @Query("DELETE FROM black_list WHERE $FOLDER_PATH_COLUMN = :path")
  suspend fun deleteFolder(path: String)
}