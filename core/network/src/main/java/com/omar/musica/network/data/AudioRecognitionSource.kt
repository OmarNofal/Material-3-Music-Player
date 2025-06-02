package com.omar.musica.network.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.omar.musica.network.model.AudioRecognitionResponse
import com.omar.musica.network.model.NetworkErrorException
import com.omar.musica.network.model.NotFoundException
import com.omar.musica.network.service.AudioRecognitionService
import okhttp3.MediaType
import okhttp3.RequestBody
// OkHttp 相关的导入将不再直接需要用于文件处理
// import okhttp3.MediaType.Companion.toMediaTypeOrNull // 如果其他地方不用，可以移除
// import okhttp3.MultipartBody // 移除
// import okhttp3.RequestBody.Companion.asRequestBody // 移除
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecognitionSource @Inject constructor(
  private val audioRecognitionService: AudioRecognitionService,
) {

  companion object {
    private const val ACR_DATA_TYPE = "audio"
    private const val ACR_SIGNATURE_VERSION = "1"
    private const val ACR_HTTP_METHOD = "POST"
    private const val ACR_HTTP_URI = "/v1/identify"
  }

  @RequiresApi(Build.VERSION_CODES.O)
  suspend fun recognizeAudio(
    audioFile: File,
    accessKey: String,
    accessSecret: String,
  ): AudioRecognitionResponse {
    try {
      if (!audioFile.exists()) {
        Timber.e("Audio file does not exist: %s", audioFile.absolutePath)
        throw NetworkErrorException("音频文件不存在")
      }

      val fileSize = audioFile.length()
      if (fileSize == 0L) {
        Timber.e("Audio file is empty: %s", audioFile.absolutePath)
        throw NetworkErrorException("音频文件为空")
      }

      if (accessKey.isEmpty()) {
        Timber.e("Access Key is empty.")
        throw NetworkErrorException("访问密钥不能为空")
      }
      if (accessSecret.isEmpty()) {
        Timber.e("Access Secret (injected) is empty. Cannot generate signature.")
        throw NetworkErrorException("访问密码未配置")
      }

      val fileSizeKB = fileSize / 1024.0
      Timber.d("准备识别音频文件:")
      Timber.d("- 文件路径: %s", audioFile.absolutePath)
      Timber.d("- 文件大小: %d bytes (%.2f KB)", fileSize, fileSizeKB)
      Timber.d("- 访问密钥: %s****%s", accessKey.take(4), accessKey.takeLast(4))

      val timestamp = (System.currentTimeMillis() / 1000).toString()
      val stringToSign = "$ACR_HTTP_METHOD\n$ACR_HTTP_URI\n$accessKey\n$ACR_DATA_TYPE\n$ACR_SIGNATURE_VERSION\n$timestamp"
      val signature = generateSignature(stringToSign, accessSecret)

      Timber.d("签名参数:")
      Timber.d("- HTTP方法: %s", ACR_HTTP_METHOD)
      Timber.d("- URI: %s", ACR_HTTP_URI)
      // ... 其他日志 ...
      Timber.d("- 文件大小 (sample_bytes): %s", fileSize.toString())

      // 调用API (Retrofit Service)
      // Retrofit 会自动处理 File -> Multipart part 的转换
      // 它通常会使用文件的名称作为 part 中的 filename，并根据文件类型或默认设置 Content-Type
      val response = audioRecognitionService.recognizeAudio(
        accessKey = RequestBody.create(MediaType.parse("text/plain"), accessKey),
        dataType = RequestBody.create(MediaType.parse("text/plain"), ACR_DATA_TYPE),
        signatureVersion = RequestBody.create(MediaType.parse("text/plain"), ACR_SIGNATURE_VERSION),
        timestamp = RequestBody.create(MediaType.parse("text/plain"), timestamp),
        signature = RequestBody.create(MediaType.parse("text/plain"), signature),
        // 这是字节数量
        sampleBytes = RequestBody.create(MediaType.parse("text/plain"), fileSize.toString()),
        sample = RequestBody.create(
          MediaType.parse("application/octet-stream"),
          audioFile
        )
      )

      Timber.d("API响应状态码: %d", response.status.code)
      Timber.d("API响应消息: %s", response.status.message)

      // ... (后续的响应处理逻辑保持不变) ...
      when (response.status.code) {
        0 -> {
          Timber.i("音频识别成功")
          response.metadata?.music?.firstOrNull()?.let {
            Timber.d("识别结果: %s - %s", it.title, it.artists.joinToString { artist -> artist.name })
          }
          return response
        }
        1001 -> {
          Timber.i("未找到匹配的歌曲")
          throw NotFoundException("没有找到匹配的歌曲")
        }
        2004 -> {
          Timber.w("无法生成音频指纹 (ACR Code 2004): %s", response.status.message)
          throw NetworkErrorException("无法识别音频内容 - 请确保：\n1. 在安静环境下录制清晰的音乐\n2. 录制时长至少10秒\n3. 音量适中，避免失真")
        }
        3000 -> {
          Timber.e("ACR API 参数无效 (ACR Code 3000): %s", response.status.message)
          throw NetworkErrorException("无效参数 - ${response.status.message}")
        }
        3001 -> {
          Timber.e("ACR API 密钥无效 (ACR Code 3001): %s", response.status.message)
          throw NetworkErrorException("API密钥无效")
        }
        3003 -> {
          Timber.e("ACR API 配额已用完 (ACR Code 3003): %s", response.status.message)
          throw NetworkErrorException("API配额已用完")
        }
        else -> {
          Timber.e("音频识别失败 (ACR Code %d): %s", response.status.code, response.status.message)
          throw NetworkErrorException("识别失败: ${response.status.message} (错误码: ${response.status.code})")
        }
      }
    } catch (e: HttpException) {
      val errorBody = e.response()?.errorBody()?.string() ?: "N/A"
      Timber.e(e, "HTTP错误: %d, 响应体: %s", e.code(), errorBody)
      when (e.code()) {
        404 -> throw NotFoundException("音频识别服务不可用 (HTTP 404)")
        401 -> throw NetworkErrorException("API认证失败，请检查密钥配置 (HTTP 401)")
        429 -> throw NetworkErrorException("请求频率过高，请稍后再试 (HTTP 429)")
        400 -> throw NetworkErrorException("无效请求参数 (HTTP 400): $errorBody")
        else -> throw NetworkErrorException("网络请求失败: ${e.message()} (HTTP ${e.code()})")
      }
    } catch (e: NotFoundException) {
      Timber.i(e, "识别结果: 未找到")
      throw e
    } catch (e: NetworkErrorException) {
      Timber.w(e, "网络或API错误")
      throw e
    } catch (e: Exception) {
      Timber.e(e, "音频识别发生未知异常")
      throw NetworkErrorException("音频识别失败: ${e.message ?: "未知错误"}")
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun generateSignature(stringToSign: String, accessSecret: String): String {
    return try {
      val secretKeySpec = SecretKeySpec(accessSecret.toByteArray(), "HmacSHA1")
      val mac = Mac.getInstance("HmacSHA1")
      mac.init(secretKeySpec)
      val rawHmac = mac.doFinal(stringToSign.toByteArray())
      Base64.getEncoder().encodeToString(rawHmac)
    } catch (e: NoSuchAlgorithmException) {
      Timber.e(e, "HMAC-SHA1 算法不可用")
      throw NetworkErrorException("签名失败: HMAC-SHA1算法不可用")
    } catch (e: InvalidKeyException) {
      Timber.e(e, "用于签名的密钥无效")
      throw NetworkErrorException("签名失败: 无效的密钥")
    }
  }
}