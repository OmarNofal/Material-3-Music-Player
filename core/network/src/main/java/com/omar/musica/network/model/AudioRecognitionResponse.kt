package com.omar.musica.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 音频识别响应数据模型
 * 包含识别到的歌曲信息
 */
@Keep
data class AudioRecognitionResponse(
  @SerializedName("status")
  val status: Status,

  @SerializedName("metadata")
  val metadata: Metadata?
) {

  @Keep
  data class Status(
    @SerializedName("msg")
    val message: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("version")
    val version: String
  )

  @Keep
  data class Metadata(
    @SerializedName("music")
    val music: List<Music>?
  ) {

    @Keep
    data class Music(
      @SerializedName("external_ids")
      val externalIds: ExternalIds?,

      @SerializedName("title")
      val title: String,

      @SerializedName("artists")
      val artists: List<Artist>,

      @SerializedName("album")
      val album: Album?,

      @SerializedName("duration_ms")
      val durationMs: Long,

      @SerializedName("genres")
      val genres: List<Genre>?,

      @SerializedName("release_date")
      val releaseDate: String?
    ) {

      @Keep
      data class ExternalIds(
        @SerializedName("spotify")
        val spotify: String?,

        @SerializedName("youtube")
        val youtube: String?,

        @SerializedName("isrc")
        val isrc: String?
      )

      @Keep
      data class Artist(
        @SerializedName("name")
        val name: String
      )

      @Keep
      data class Album(
        @SerializedName("name")
        val name: String
      )

      @Keep
      data class Genre(
        @SerializedName("name")
        val name: String
      )
    }
  }
}