package com.omar.musica.tageditor.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.TagsRepository
import com.omar.musica.store.model.tags.SongTags
import com.omar.musica.tageditor.state.TagEditorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri


@HiltViewModel
class TagEditorViewModel @Inject constructor(
  private val tagsRepository: TagsRepository,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val songUri = Uri.decode(savedStateHandle.get<String>("uri").orEmpty()).toUri()
  private val _state = MutableStateFlow<TagEditorState>(TagEditorState.Loading)
  val state: StateFlow<TagEditorState> get() = _state.asStateFlow()

  init {
    viewModelScope.launch {
      loadTags()
    }
  }

  private suspend fun loadTags() {
    val songTags = tagsRepository.getSongTags(songUri)
    _state.value = TagEditorState.Loaded(songTags)
  }

  fun saveTags(songTags: SongTags) {
    viewModelScope.launch {
      val currentState = state.value as TagEditorState.Loaded
      _state.value = currentState.copy(isSaving = true, isSaved = false)
      try {
        tagsRepository.editTags(songUri, songTags)
        _state.getAndUpdate {
          if (it is TagEditorState.Loaded)
            it.copy(isSaved = true, isSaving = false, isFailed = false)
          else
            TagEditorState.Loading
        }
      } catch (e: Exception) {
        _state.getAndUpdate {
          if (it is TagEditorState.Loaded)
            it.copy(isSaved = false, isSaving = false, isFailed = true)
          else
            TagEditorState.Loading
        }
      }
    }
  }
}