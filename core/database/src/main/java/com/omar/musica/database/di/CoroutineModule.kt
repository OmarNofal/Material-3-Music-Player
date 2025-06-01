package com.omar.musica.database.di // 或者您项目的其他合适位置

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationCoroutineScope // 自定义限定符

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher // 自定义限定符

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

  @Provides
  @Singleton
  @IoDispatcher
  fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Singleton
  @ApplicationCoroutineScope
  fun provideApplicationCoroutineScope(
    @IoDispatcher ioDispatcher: CoroutineDispatcher
  ): CoroutineScope {
    // SupervisorJob 确保一个子协程失败不会影响其他子协程或作用域本身
    return CoroutineScope(SupervisorJob() + ioDispatcher)
  }
}