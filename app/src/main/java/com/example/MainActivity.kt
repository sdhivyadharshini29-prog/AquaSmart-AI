package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aquasmart.data.local.AquaSmartDatabase
import com.example.aquasmart.data.repository.AquaSmartRepository
import com.example.aquasmart.ui.AquaSmartApp
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.AquaSmartViewModelFactory
import com.example.ui.theme.AquaSmartTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AquaSmartViewModel by viewModels {
        val database = AquaSmartDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = AquaSmartRepository(database.aquaSmartDao())
        AquaSmartViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaSmartTheme {
                AquaSmartApp(viewModel = viewModel)
            }
        }
    }
}
