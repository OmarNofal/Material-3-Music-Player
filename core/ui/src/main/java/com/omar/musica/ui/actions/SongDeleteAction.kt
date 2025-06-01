package com.omar.musica.ui.actions

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.model.song.Song

fun interface SongDeleteAction {
  fun deleteSongs(songs: List<Song>)
}


class AndroidRAboveDeleter(
  private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
  private val context: Context
) : SongDeleteAction {

  @RequiresApi(30)
  override fun deleteSongs(songs: List<Song>) {
    if (songs.isEmpty()) return
    val senderRequest = getIntentSenderRequest(context, songs[0].uri)
    activityResultLauncher.launch(senderRequest)
  }

  @RequiresApi(30)
  private fun getIntentSenderRequest(context: Context, uri: Uri): IntentSenderRequest {
    return with(context) {

      val deleteRequest =
        android.provider.MediaStore.createDeleteRequest(contentResolver, listOf(uri))

      IntentSenderRequest.Builder(deleteRequest)
        .setFillInIntent(null)
        .setFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
        .build()
    }
  }

}

class AndroidQBelowDeleter(
  private val mediaRepository: MediaRepository
) : SongDeleteAction {

  override fun deleteSongs(songs: List<Song>) {
    songs.forEach { mediaRepository.deleteSong(it) }
  }

}

@Composable
fun deleteRequestLauncher(): AndroidRAboveDeleter {
  val context = LocalContext.current
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult(),
    onResult = {
      if (it.resultCode == Activity.RESULT_OK) {
        Toast.makeText(context, "Song deleted", Toast.LENGTH_SHORT).show()
      }
    })
  return remember {
    AndroidRAboveDeleter(launcher, context)
  }
}

@Composable
fun rememberSongDeleter(mediaRepository: MediaRepository): SongDeleteAction =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
    deleteRequestLauncher()
  else
    remember { AndroidQBelowDeleter(mediaRepository = mediaRepository) }
