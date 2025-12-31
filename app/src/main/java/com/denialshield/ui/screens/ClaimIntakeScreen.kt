package com.denialshield.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denialshield.data.model.DenialClaim
import com.denialshield.ui.viewmodel.MainViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimIntakeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    var providerName by remember { mutableStateOf("") }
    var claimId by remember { mutableStateOf("") }
    var denialReasonCode by remember { mutableStateOf("") }
    var denialReasonDescription by remember { mutableStateOf("") }
    
    val selectedUris = remember { mutableStateListOf<Uri>() }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for Gallery (Multiple)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        selectedUris.addAll(uris)
    }

    // Launcher for Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            selectedUris.add(tempCameraUri!!)
        }
    }

    // Launcher for Files (PDFs)
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedUris.addAll(uris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Denial Intake") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Step 1: Basic Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            OutlinedTextField(
                value = providerName,
                onValueChange = { providerName = it },
                label = { Text("Health Care Provider Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = claimId,
                onValueChange = { claimId = it },
                label = { Text("Claim Number / ID") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Divider()
            
            Text(
                "Step 2: Denial Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            OutlinedTextField(
                value = denialReasonCode,
                onValueChange = { denialReasonCode = it },
                label = { Text("Denial Reason Code (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = denialReasonDescription,
                onValueChange = { denialReasonDescription = it },
                label = { Text("Brief Description of Denial") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Divider()
            
            Text(
                "Step 3: Documents (Evidence)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (selectedUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(100.dp)
                ) {
                    items(selectedUris) { uri ->
                        Box {
                            if (uri.toString().contains("pdf")) {
                                Surface(
                                    modifier = Modifier.size(100.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Icon(
                                        Icons.Default.Description, 
                                        contentDescription = null,
                                        modifier = Modifier.padding(24.dp)
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            IconButton(
                                onClick = { selectedUris.remove(uri) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val uri = createTempImageUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Capture")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Gallery")
                }
                OutlinedButton(
                    onClick = { fileLauncher.launch("*/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Files")
                }
            }
            
            Button(
                onClick = {
                    val claim = DenialClaim(
                        providerName = providerName,
                        claimId = claimId,
                        denialReasonCode = denialReasonCode,
                        denialReasonDescription = denialReasonDescription,
                        status = if (selectedUris.isEmpty()) "PENDING" else "PROCESSING"
                    )
                    viewModel.addClaim(claim) { id ->
                        if (selectedUris.isNotEmpty()) {
                            viewModel.processDocuments(id, selectedUris)
                        }
                        onNavigateToDetail(id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedUris.isEmpty()) "Save Claim" else "Analyze and Generate Rebuttal")
            }
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val tempFile = File(context.cacheDir, "camera_${UUID.randomUUID()}.jpg")
    return androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
