package com.omar.musica.tageditor.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.omar.musica.ui.showShortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder


@Composable
fun CoverArtPicker(
    showDialog: Boolean,
    albumName: String,
    songTitle: String,
    onUserPickedBitmap: (Bitmap?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            if (it == null) return@rememberLauncherForActivityResult
            scope.launch(Dispatchers.IO) {
                context.contentResolver.openInputStream(it).use { iStream ->
                    if (iStream == null) {
                        context.showShortToast("Failed to pick image")
                    } else {
                        val bitmap = BitmapFactory.decodeStream(iStream)
                        onUserPickedBitmap(bitmap)
                    }
                }
            }
        })

    if (!showDialog) return
    CoverArtPickerDialog(
        onPickFromDevice = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            onDismissRequest()
        },
        onSearchWeb = {
            context.searchCoverArtOnGoogle(albumName, songTitle)
            onDismissRequest()
        },
        onDelete = {
            onUserPickedBitmap(null)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest
    )


}

@Composable
fun CoverArtPickerDialog(
    onPickFromDevice: () -> Unit,
    onSearchWeb: () -> Unit,
    onDelete: () -> Unit,
    onDismissRequest: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Modify Artwork") },
        text = {
            Column {
                Option(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)),
                    text = "Pick from the gallery",
                    icon = Icons.Rounded.Image,
                    onClick = onPickFromDevice
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 42.dp))
                Option(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)),
                    text = "Search the web",
                    icon = Icons.Rounded.Language,
                    onClick = onSearchWeb
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 42.dp))
                Option(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)),
                    text = "Delete artwork",
                    icon = Icons.Rounded.Delete,
                    onClick = onDelete
                )
            }
        }
    )
}

@Composable
private fun Option(
    modifier: Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

fun Context.searchCoverArtOnGoogle(albumName: String, songTitle: String) {
    val searchTerm = if (albumName.isEmpty() || albumName.isBlank()) {
        "$songTitle cover art"
    } else {
        "$albumName cover art"
    }
    val url = "https://google.com/search?q=${URLEncoder.encode(searchTerm)}&sclient=img"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}