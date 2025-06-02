package com.omar.musica.store

import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.store.model.album.AlbumWithSongs
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.store.model.song.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File
import javax.inject.Inject


class FoldersRepository @Inject constructor(
  val mediaRepository: MediaRepository
) {

  private val scope = CoroutineScope(Dispatchers.Default)

  /**
   * All the folders of the device alongside their songs
   */
  val folders: StateFlow<List<AlbumWithSongs>> = mediaRepository.songsFlow
    .map {
      val songs = it.songs
      // 核心改动：按文件夹分组而不是按albumName分组
      val foldersGroups = songs
        .groupBy { song -> getParentFolderName(song.filePath) }
        .filter { entry -> entry.key.isNotEmpty() }

      var counter = 1
      foldersGroups.map { entry ->
        val firstSong = entry.value[0]
        AlbumWithSongs(
          BasicAlbumInfo(
            counter++,
            entry.key, // 文件夹名
            getParentFolderPath(firstSong.filePath), // 显示完整文件夹路径
            entry.value.size
          ),
          entry.value.map { AlbumSong(it, null) } // 文件夹没有轨道编号概念
        )
      }
    }
    .stateIn(
      scope,
      SharingStarted.Eagerly,
      listOf()
    )

  /**
   * Contains simplified information about all folders
   * Used inside the Folders Screen
   */
  val basicFolders: StateFlow<List<BasicAlbum>> = folders
    .map { folders -> folders.map { BasicAlbum(it.albumInfo, it.songs.firstOrNull()?.song) } }
    .stateIn(
      scope,
      SharingStarted.Eagerly,
      listOf()
    )

  fun getFolderWithSongs(folderId: Int) =
    folders.map { allFolders ->
      allFolders
        .firstOrNull { it.albumInfo.id == folderId }
        ?.let {
          // 按文件名排序（文件夹内没有轨道编号）
          val sortedSongs = it.songs.sortedBy { song -> song.song.metadata.title }
          it.copy(songs = sortedSongs)
        }
    }

  fun getSongFolderId(song: Song): Int? {
    val folder = folders.value.firstOrNull { it.songs.map { it.song }.any { it.uri == song.uri } }
    val folderId = folder?.albumInfo?.id
    return folderId
  }

  /**
   * 从文件路径提取父文件夹名
   * "/sdcard/Music/周杰伦/歌曲.mp3" -> "周杰伦"
   */
  private fun getParentFolderName(filePath: String): String {
    return try {
      File(filePath).parentFile?.name ?: "Unknown Folder"
    } catch (e: Exception) {
      "Unknown Folder"
    }
  }

  /**
   * 从文件路径提取完整的文件夹路径
   * "/sdcard/Music/周杰伦/歌曲.mp3" -> "/sdcard/Music/周杰伦"
   */
  private fun getParentFolderPath(filePath: String): String {
    return try {
      File(filePath).parentFile?.absolutePath ?: "Unknown Folder"
    } catch (e: Exception) {
      "Unknown Folder"
    }
  }
}