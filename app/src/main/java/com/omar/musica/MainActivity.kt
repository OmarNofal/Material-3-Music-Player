package com.omar.musica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.omar.musica.store.UserPreferencesRepository
import com.omar.musica.ui.MusicaApp
import com.omar.musica.ui.theme.MusicaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialUserPreferences =
            runBlocking { userPreferencesRepository.userSettingsFlow.first() }

        setContent {

            val userPreferences by userPreferencesRepository.userSettingsFlow.collectAsState(
                initial = initialUserPreferences
            )


            MusicaTheme(
                userPreferences = userPreferences,
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
