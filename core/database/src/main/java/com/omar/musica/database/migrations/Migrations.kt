package com.omar.musica.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.omar.musica.database.entities.ALBUM_NAME_COLUMN
import com.omar.musica.database.entities.LISTENING_SESSION_TABLE
import com.omar.musica.database.entities.SONG_NAME_COLUMN
import com.omar.musica.database.entities.SONG_URI_STRING_COLUMN
import com.omar.musica.database.entities.lyrics.LYRICS_TABLE


val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a new table with the updated schema
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS $LYRICS_TABLE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "title TEXT," +
                    "album TEXT," +
                    "artist TEXT," +
                    "plainLyrics TEXT," +
                    "syncedLyrics TEXT)"
        )
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a new table with the updated schema
        database.execSQL(
            "ALTER TABLE $LISTENING_SESSION_TABLE ADD COLUMN $SONG_URI_STRING_COLUMN TEXT NOT NULL DEFAULT ''"
        )
        database.execSQL(
            "ALTER TABLE $LISTENING_SESSION_TABLE ADD COLUMN $SONG_NAME_COLUMN TEXT NOT NULL DEFAULT ''"
        )
        database.execSQL(
            "ALTER TABLE $LISTENING_SESSION_TABLE ADD COLUMN $ALBUM_NAME_COLUMN TEXT NOT NULL DEFAULT ''"
        )
    }
}