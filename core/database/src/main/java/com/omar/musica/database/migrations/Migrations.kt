package com.omar.musica.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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