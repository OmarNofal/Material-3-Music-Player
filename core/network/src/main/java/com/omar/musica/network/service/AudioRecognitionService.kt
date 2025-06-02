package com.omar.musica.network.service

import com.omar.musica.network.model.AudioRecognitionResponse
import okhttp3.RequestBody
// import okhttp3.MultipartBody //不再需要
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File // 导入 File

/**
 * 音频识别服务接口
 * 用于与ACRCloud音频识别API进行通信
 * ... (其他注释保持不变) ...
 */
interface AudioRecognitionService {

  companion object {
    const val BASE_URL = "https://identify-cn-north-1.acrcloud.cn/"
  }

  /**
   * 发送音频数据进行识别
   * ... (其他注释保持不变) ...
   * @param sample 音频数据文件 (Retrofit 将处理 File 到 multipart part 的转换)
   * @return 识别结果
   */
  @Multipart
  @POST("v1/identify")
  suspend fun recognizeAudio(
    @Part("access_key") accessKey: RequestBody,
    @Part("data_type") dataType: RequestBody,
    @Part("signature_version") signatureVersion: RequestBody,
    @Part("timestamp") timestamp: RequestBody,
    @Part("signature") signature: RequestBody,
    @Part("sample_bytes") sampleBytes: RequestBody,
    @Part("sample") sample: RequestBody,
  ): AudioRecognitionResponse
}