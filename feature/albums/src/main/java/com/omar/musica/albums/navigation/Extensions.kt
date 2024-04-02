package com.omar.musica.albums.navigation

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeFromBase64(): String {
    return Base64.decode(this).toString(charset("UTF-8"))
}

@OptIn(ExperimentalEncodingApi::class)
fun String.encodeToBase64(): String {
    return Base64.encode(this.toByteArray(charset("UTF-8")))
}