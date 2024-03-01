package com.omar.musica.settings.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun GeneralSettingsItem(
    modifier: Modifier,
    title: String,
    subtitle: String? = null,
    info: SettingInfo? = null,
    content: @Composable RowScope.() -> Unit = {}
) {


    var infoDialogVisible by remember {
        mutableStateOf(false)
    }

    if (info != null)
        InformationDialog(visible = infoDialogVisible, info = info) {
            infoDialogVisible = false
        }

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp)
            if (subtitle == null) return@Column
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 12.sp
            )
        }


        if (info != null) {
            IconButton(onClick = { infoDialogVisible = true }) {
                Icon(imageVector = Icons.Rounded.Info, contentDescription = "More Info")
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        content()

    }

}

data class SettingInfo(
    val title: String,
    val text: String,
    val icon: ImageVector
)


@Composable
fun SwitchSettingsItem(
    modifier: Modifier,
    title: String,
    subtitle: String? = null,
    info: SettingInfo? = null,
    toggled: Boolean = false,
    onToggle: () -> Unit = {}
) {

    GeneralSettingsItem(
        modifier = modifier
            .clickable { onToggle() }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        title = title,
        subtitle = subtitle,
        info = info,
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Switch(modifier = Modifier.padding(end = 16.dp), checked = toggled, onCheckedChange = { onToggle() })
    }

}


@Composable
fun InformationDialog(
    visible: Boolean,
    info: SettingInfo,
    onDismissRequest: () -> Unit
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(text = "Ok") } },
        confirmButton = { },
        icon = { Icon(info.icon, contentDescription = null) },
        title = { Text(text = info.title) },
        text = {
            Text(text = info.text)
        }
    )
}