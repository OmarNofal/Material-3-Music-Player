package com.omar.musica.tageditor.state

import com.omar.musica.tageditor.SongMetadata

sealed interface TagEditorState {
    data object Loading: TagEditorState
    data class Loaded(val songMetadata: SongMetadata): TagEditorState
}