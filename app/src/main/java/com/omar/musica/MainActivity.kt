package com.omar.musica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.UserPreferencesRepository
import com.omar.musica.ui.MusicaApp
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.common.rememberCommonSongsActions
import com.omar.musica.ui.model.toUiModel
import com.omar.musica.ui.theme.MusicaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var playbackManager: PlaybackManager

    @Inject
    lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialUserPreferences =
            runBlocking { userPreferencesRepository.userSettingsFlow.first().toUiModel() }

        val userPreferencesFlow = userPreferencesRepository.userSettingsFlow.map { it.toUiModel() }

        setContent {

            val userPreferences by userPreferencesFlow
                .collectAsState(
                    initial = initialUserPreferences
                )

            MusicaTheme(
                userPreferences = userPreferences,
            ) {

                val commonSongsActions = rememberCommonSongsActions(playbackManager, mediaRepository)

                CompositionLocalProvider(
                    LocalUserPreferences provides userPreferences,
                    LocalCommonSongsAction provides commonSongsActions
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        MusicaApp(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}