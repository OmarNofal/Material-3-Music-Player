package com.omar.musica.ui.common

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Composable
fun songDeleteAndroid10(
    songUri: Uri,
    onDelete: () -> Unit
): SongDeleterAndroid10Handler {

    val context = LocalContext.current

    val contract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK)
                onDelete()
        })

    return remember {
        SongDeleterAndroid10Handler(context, songUri) {
            contract.launch(it)
        }
    }
}

class SongDeleterAndroid10Handler(
    private val context: Context,
    private val uri: Uri,
    private val onDelete: (IntentSenderRequest) -> Unit
) {
    fun delete() {
        val contentResolver = context.contentResolver
        try {
            contentResolver.delete(uri, null, null);
        } catch (securityException: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException =
                    securityException as RecoverableSecurityException;
                val senderRequest = IntentSenderRequest.Builder(
                    recoverableSecurityException.userAction
                        .actionIntent.intentSender
                ).build();
                onDelete(senderRequest)
            }
        }
    }
}
