package com.omar.musica.network.di

import com.omar.musica.network.service.AudioRecognitionService
import com.omar.musica.network.service.LyricsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LyricsRetrofitService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioRecognitionRetrofitService

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideLyricsService(
        @LyricsRetrofitService lyricsRetrofitService: Retrofit
    ) = lyricsRetrofitService.create<LyricsService>()

    @Provides
    @Singleton
    fun provideAudioRecognitionService(
        @AudioRecognitionRetrofitService audioRecognitionService: Retrofit,
    ) = audioRecognitionService.create<AudioRecognitionService>()

    @LyricsRetrofitService
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(LyricsService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @AudioRecognitionRetrofitService
    @Provides
    @Singleton
    fun provideAudioRecognitionRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(AudioRecognitionService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}