package com.omar.musica.audiosearch

import android.content.Context
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 音频文件工具类
 * 用于保存和管理录制的音频文件
 */
object AudioFileUtils {

  private const val AUDIO_DIR = "recorded_audio"
  private const val AUDIO_FILE_EXTENSION = ".pcm"

  /**
   * 保存音频数据到文件
   * @param context 上下文
   * @param audioData 音频数据
   * @param fileName 文件名（不包含扩展名）
   * @return 保存的文件，如果失败则返回null
   */
  fun saveAudioToFile(
    context: Context,
    audioData: ByteArray,
    fileName: String = "recorded_audio_${System.currentTimeMillis()}"
  ): File? {
    return try {
      // 创建音频目录
      val audioDir = File(context.getExternalFilesDir(null), AUDIO_DIR)
      if (!audioDir.exists()) {
        audioDir.mkdirs()
      }

      // 创建音频文件
      val audioFile = File(audioDir, "$fileName$AUDIO_FILE_EXTENSION")

      // 写入音频数据
      FileOutputStream(audioFile).use { outputStream ->
        outputStream.write(audioData)
        outputStream.flush()
      }

      Timber.Forest.d("音频文件已保存: ${audioFile.absolutePath}, 大小: ${audioData.size} bytes")
      audioFile

    } catch (e: IOException) {
      Timber.Forest.e(e, "保存音频文件失败")
      null
    } catch (e: Exception) {
      Timber.Forest.e(e, "保存音频文件时发生未知错误")
      null
    }
  }

  /**
   * 删除旧的音频文件
   * @param context 上下文
   * @param maxFiles 保留的最大文件数
   */
  fun cleanupOldAudioFiles(context: Context, maxFiles: Int = 5) {
    try {
      val audioDir = File(context.getExternalFilesDir(null), AUDIO_DIR)
      if (!audioDir.exists()) return

      val audioFiles = audioDir.listFiles { file ->
        file.isFile && file.name.endsWith(AUDIO_FILE_EXTENSION)
      }?.sortedByDescending { it.lastModified() }

      if (audioFiles != null && audioFiles.size > maxFiles) {
        val filesToDelete = audioFiles.drop(maxFiles)
        filesToDelete.forEach { file ->
          if (file.delete()) {
            Timber.Forest.d("删除旧音频文件: ${file.name}")
          }
        }
      }

    } catch (e: Exception) {
      Timber.Forest.e(e, "清理旧音频文件失败")
    }
  }

  /**
   * 获取音频文件列表
   * @param context 上下文
   * @return 音频文件列表
   */
  fun getAudioFiles(context: Context): List<File> {
    return try {
      val audioDir = File(context.getExternalFilesDir(null), AUDIO_DIR)
      if (!audioDir.exists()) {
        emptyList()
      } else {
        audioDir.listFiles { file ->
          file.isFile && file.name.endsWith(AUDIO_FILE_EXTENSION)
        }?.sortedByDescending { it.lastModified() }?.toList() ?: emptyList()
      }
    } catch (e: Exception) {
      Timber.Forest.e(e, "获取音频文件列表失败")
      emptyList()
    }
  }

  /**
   * 格式化文件大小
   * @param sizeInBytes 文件大小（字节）
   * @return 格式化的文件大小字符串
   */
  fun formatFileSize(sizeInBytes: Long): String {
    return when {
      sizeInBytes < 1024 -> "$sizeInBytes B"
      sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
      else -> "${sizeInBytes / (1024 * 1024)} MB"
    }
  }
}