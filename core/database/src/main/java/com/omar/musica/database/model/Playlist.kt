package com.omar.musica.database.model

import com.omar.musica.database.entities.PlaylistEntity


typealias PlaylistWithSongsUris =
    Map<PlaylistEntity, List<String>>