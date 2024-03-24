package com.omar.musica.ui.common

import androidx.compose.runtime.mutableStateListOf

data class MultiSelectState<T>(
    val selected: MutableList<T> = mutableStateListOf()
) {
    private fun select(item: T) {
        selected.add(item)
    }

    fun toggle(item: T) {
        if (selected.contains(item)) {
            deselect(item)
        } else {
            select(item)
        }
    }

    fun clear() {
        selected.clear()
    }

    private fun deselect(item: T) {
        selected.remove(item)
    }
}