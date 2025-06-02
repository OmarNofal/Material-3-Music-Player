package com.omar.musica.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 音频识别请求数据模型
 * 用于向音频识别服务发送识别请求
 */
@Keep
data class AudioRecognitionRequest(
  @SerializedName("access_key")
  val accessKey: String,

  @SerializedName("access_secret")
  val accessSecret: String,

  @SerializedName("data_type")
  val dataType: String = "audio",

  @SerializedName("sample_bytes")
  val sampleBytes: String, // Base64编码的音频数据

  @SerializedName("sample_rate")
  val sampleRate: Int = 44100,

  @SerializedName("channels")
  val channels: Int = 1,

  @SerializedName("timestamp")
  val timestamp: Long = System.currentTimeMillis() / 1000
)