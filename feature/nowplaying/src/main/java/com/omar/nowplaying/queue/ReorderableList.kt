package com.omar.nowplaying.queue

import androidx.compose.runtime.MutableState


class ReorderableList<T>(
    val items: MutableState<MutableList<T>>,
    private val updateOrderCallback: (from: Int, to: Int) -> Unit
) {

    private var isDragging = false
    private var startDragIndex: Int = -1
    private var endDragIndex: Int = -1

    fun onDragStarted(index: Int) {
        if (!isDragging) {
            startDragIndex = index
            isDragging = true
        }
    }

    fun onDragStopped() {
        if (startDragIndex == -1 || endDragIndex == -1) return
        else updateOrderCallback(startDragIndex, endDragIndex)
        startDragIndex = -1
        endDragIndex = -1
        isDragging = false
    }

    fun reorder(from: Int, to: Int) {
        items.value = items.value.toMutableList().apply { add(to, removeAt(from)) }
        endDragIndex = to
    }

}