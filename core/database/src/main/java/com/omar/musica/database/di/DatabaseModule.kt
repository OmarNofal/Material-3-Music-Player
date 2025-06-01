package com.omar.musica.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.omar.musica.database.MusicaDatabase
import com.omar.musica.database.entities.DB_NAME
import com.omar.musica.database.migrations.MIGRATION_3_4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Singleton
  @Provides
  fun provideRoomDatabase(
    @ApplicationContext context: Context,
    databaseInitCallback: DatabaseInitializationCallback
  ): MusicaDatabase =
    Room.databaseBuilder(context, MusicaDatabase::class.java, name = DB_NAME)
      .addMigrations(MIGRATION_3_4)
      .addCallback(databaseInitCallback)
      .fallbackToDestructiveMigration()
      .build()

  @Singleton
  @Provides
  fun providePlaylistDao(
    appDatabase: MusicaDatabase
  ) = appDatabase.playlistsDao()

  @Singleton
  @Provides
  fun provideBlacklistedFoldersDao(
    appDatabase: MusicaDatabase
  ) = appDatabase.blacklistDao()

  @Singleton
  @Provides
  fun provideQueueDao(
    appDatabase: MusicaDatabase
  ) = appDatabase.queueDao()

  @Singleton
  @Provides
  fun provideActivityDao(
    appDatabase: MusicaDatabase
  ) = appDatabase.activityDao()

  @Singleton
  @Provides
  fun provideLyricsDao(
    appDatabase: MusicaDatabase
  ) = appDatabase.lyricsDao()
}
