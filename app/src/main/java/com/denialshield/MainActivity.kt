package com.denialshield

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        val prefs = getSharedPreferences("denial_shield_prefs", Context.MODE_PRIVATE)
        val disclaimerAccepted = prefs.getBoolean("disclaimer_accepted", false)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "denial-shield-db"
        ).build()

        val repository = DenialRepository(db.denialDao())
        val documentProcessor = DocumentProcessor(applicationContext)
        val aiGenerator = AiRebuttalGenerator(applicationContext)

        setContent {
            var showDisclaimer by remember { mutableStateOf(!disclaimerAccepted) }

            DenialShieldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showDisclaimer) {
                        DisclaimerScreen {
                            prefs.edit().putBoolean("disclaimer_accepted", true).apply()
                            showDisclaimer = false
                        }
                    } else {
                        val navController = rememberNavController()
                        val viewModel: MainViewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                    @Suppress("UNCHECKED_CAST")
                                    return MainViewModel(repository, documentProcessor, aiGenerator) as T
                                }
                            }
                        )
                        DenialShieldNavGraph(navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DisclaimerScreen(onUnderstandClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.8f))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Disclaimer",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app uses on-device AI and is for informational purposes only. It is not a substitute for professional legal or medical advice. Always consult with a qualified professional for any legal or medical concerns.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onUnderstandClicked) {
            Text("I understand")
        }
    }
}
