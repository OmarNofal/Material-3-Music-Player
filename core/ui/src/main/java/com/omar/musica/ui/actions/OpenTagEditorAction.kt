package com.omar.musica.ui.actions

import android.net.Uri


interface OpenTagEditorAction {
    fun open(songUri: Uri)
}