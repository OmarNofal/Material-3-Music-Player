package com.omar.musica.database.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.database.entities.playlist.PlaylistEntity // 假设您要预填充 PlaylistEntity
import com.omar.musica.model.playlist.PlaylistInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider // 非常重要，用于延迟获取 DAO

class DatabaseInitializationCallback @Inject constructor(
  // 使用 Provider<PlaylistDao> 来避免在数据库构建期间出现循环依赖
  // 因为 PlaylistDao 依赖于 MusicaDatabase，而 MusicaDatabase 的构建又依赖于此回调
  private val playlistDaoProvider: Provider<PlaylistDao>,
  @ApplicationCoroutineScope private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {

  override fun onCreate(db: SupportSQLiteDatabase) {
    super.onCreate(db)
    // 数据库首次创建时调用
    // onCreate 执行时，数据库结构已创建，但数据为空
    // 我们在这里通过 Provider 获取 DAO 实例
    val playlistDao = playlistDaoProvider.get()
    applicationScope.launch {
      populateInitialPlaylists(playlistDao)
    }
  }

  private suspend fun populateInitialPlaylists(playlistDao: PlaylistDao) {
    // 创建两个初始播放列表
    val recentlyPlayed = PlaylistEntity(
      PlaylistInfo.RECENT_PLAYED_PLAYLIST_ID,
      "Recently Played",
    )
    val favorites = PlaylistEntity(
      PlaylistInfo.FAVORITE_PLAYLIST_ID,
      "Favorites",
    )
    playlistDao.createPlaylist(recentlyPlayed)
    playlistDao.createPlaylist(favorites)
  }
}