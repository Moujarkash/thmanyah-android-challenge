package com.mod.thmanyah_android_challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mod.thmanyah_android_challenge.di.apiModule
import com.mod.thmanyah_android_challenge.di.networkModule
import com.mod.thmanyah_android_challenge.di.repositoryModule
import com.mod.thmanyah_android_challenge.di.viewModelModule
import com.mod.thmanyah_android_challenge.ui.navigation.ThmanyahNavigation
import com.mod.thmanyah_android_challenge.ui.theme.ThmanyahAndroidChallengeTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(
                networkModule,
                apiModule,
                repositoryModule,
                viewModelModule
            )
        }

        enableEdgeToEdge()
        setContent {
            ThmanyahAndroidChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ThmanyahNavigation()
                }
            }
        }
    }
}