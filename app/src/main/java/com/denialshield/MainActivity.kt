package com.denialshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.denialshield.data.local.AppDatabase
import com.denialshield.data.repository.DenialRepository
import com.denialshield.ui.navigation.DenialShieldNavGraph
import com.denialshield.ui.theme.DenialShieldTheme
import com.denialshield.ui.viewmodel.MainViewModel
import com.denialshield.utils.AiRebuttalGenerator
import com.denialshield.utils.DocumentProcessor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "denial-shield-db"
        ).build()
        
        val repository = DenialRepository(db.denialDao())
        val documentProcessor = DocumentProcessor(applicationContext)
        val aiGenerator = AiRebuttalGenerator(applicationContext)

        setContent {
            DenialShieldTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(repository, documentProcessor, aiGenerator) as T
                        }
                    }
                )
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DenialShieldNavGraph(navController, viewModel)
                }
            }
        }
    }
}
