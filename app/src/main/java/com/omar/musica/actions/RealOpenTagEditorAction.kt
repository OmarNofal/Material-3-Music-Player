package com.omar.musica.actions

import android.net.Uri
import androidx.navigation.NavHostController
import com.omar.musica.tageditor.navigation.TAG_EDITOR_GRAPH
import com.omar.musica.ui.actions.OpenTagEditorAction


class RealOpenTagEditorAction(
    private val navHostController: NavHostController
) : OpenTagEditorAction {

    override fun open(songUri: Uri) {
        val encodedUri = Uri.encode(songUri.toString())
        navHostController.navigate("$TAG_EDITOR_GRAPH/$encodedUri")
    }

}