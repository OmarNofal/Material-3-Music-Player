package com.omar.musica.albums.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailPortraitTopBar(
    modifier: Modifier,
    name: String,
    collapsePercentage: Float,
    onBarHeightChanged: (Int) -> Unit
) {

    TopAppBar(
        modifier = modifier
            .onGloballyPositioned {
                onBarHeightChanged(it.size.height)
            },
        title = {
            Text(
                modifier = Modifier.graphicsLayer {
                    alpha = collapsePercentage
                },
                text = name,
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0x33000000), alpha = 1-collapsePercentage)
                    }
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )

}