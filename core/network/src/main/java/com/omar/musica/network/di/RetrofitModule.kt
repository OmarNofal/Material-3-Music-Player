package com.omar.musica.network.di

import android.content.Context
import com.omar.musica.network.service.LyricsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


annotation class LyricsRetrofitService

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {


    @Provides
    fun provideLyricsService(
        @LyricsRetrofitService lyricsRetrofitService: Retrofit
    ) = lyricsRetrofitService.create<LyricsService>()

    @LyricsRetrofitService
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(LyricsService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}