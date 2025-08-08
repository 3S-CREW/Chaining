package com.example.chaining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.chaining.ui.login.LoginScreen
import com.example.chaining.ui.screen.HomeScreen
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            var isLoggedIn by remember { mutableStateOf(Firebase.auth.currentUser != null) }

            if (isLoggedIn) {
                HomeScreen()
            } else {
                LoginScreen {
                    isLoggedIn = true
                }
            }
        }
    }
}