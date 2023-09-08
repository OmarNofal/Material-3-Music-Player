package com.omar.musica

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omar.musica.songs.ui.SongsScreen
import com.omar.musica.ui.theme.MusicaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            MusicaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = "songs"
                    ) {

                        composable(
                            route = "songs"
                        ) {
                            SongsScreen(Modifier.fillMaxSize())
                        }

                    }
                }
            }
        }
    }


    override fun onDestroy() {
        Log.d(TAG, "On destroy")
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
