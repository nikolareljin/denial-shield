package com.denialshield.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denialshield.data.model.DenialClaim
import com.denialshield.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimIntakeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    var providerName by remember { mutableStateOf("") }
    var claimId by remember { mutableStateOf("") }
    var denialReasonCode by remember { mutableStateOf("") }
    var denialReasonDescription by remember { mutableStateOf("") }
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // We need a claim ID first. 
            // In a real app, we might save the claim first, then attach evidence.
            viewModel.addClaim(DenialClaim(
                providerName = providerName,
                claimId = claimId,
                denialReasonCode = denialReasonCode,
                denialReasonDescription = denialReasonDescription,
                status = "PROCESSING"
            )) { id ->
                viewModel.processDocument(id, it, isPdf = false) // Assuming photo
                onNavigateToDetail(id)
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addClaim(DenialClaim(
                providerName = providerName,
                claimId = claimId,
                denialReasonCode = denialReasonCode,
                denialReasonDescription = denialReasonDescription,
                status = "PROCESSING"
            )) { id ->
                viewModel.processDocument(id, it, isPdf = true)
                onNavigateToDetail(id)
            }
        }
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
                "Step 3: Upload Documents",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                "Upload a photo of the denial letter or a PDF to extract policy language automatically.",
                style = MaterialTheme.typography.bodySmall
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { photoLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select Photo")
                }
                Button(
                    onClick = { pdfLauncher.launch("application/pdf") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select PDF")
                }
            }
            
            Button(
                onClick = {
                    viewModel.addClaim(DenialClaim(
                        providerName = providerName,
                        claimId = claimId,
                        denialReasonCode = denialReasonCode,
                        denialReasonDescription = denialReasonDescription
                    )) { id ->
                        onNavigateToDetail(id)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Save and Continue Manually")
            }
        }
    }
}
