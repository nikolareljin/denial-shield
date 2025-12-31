package com.denialshield.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denialshield.data.model.DenialClaim
import com.denialshield.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailScreen(
    claimId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val claims by viewModel.allClaims.collectAsState()
    val claim = claims.find { it.id == claimId }
    val isProcessing by viewModel.isProcessing.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Claim Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (claim?.generatedRebuttal?.isNotEmpty() == true) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putcharExtra(Intent.EXTRA_SUBJECT, "Medical Appeal Rebuttal")
                                putcharExtra(Intent.EXTRA_TEXT, claim.generatedRebuttal)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Rebuttal"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (claim == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoSection("Provider", claim.providerName)
                InfoSection("Claim ID", claim.claimId)
                InfoSection("Reason", "${claim.denialReasonDescription} (${claim.denialReasonCode})")
                InfoSection("Status", claim.status)

                Divider()

                Text(
                    "Extracted Policy Language",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (claim.policyLanguageCited.isEmpty()) {
                    Text("No policy language extracted yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(claim.policyLanguageCited, style = MaterialTheme.typography.bodyMedium)
                }

                Divider()

                if (isProcessing) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text("AI is generating your rebuttal...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    if (claim.generatedRebuttal.isEmpty()) {
                        Button(
                            onClick = { viewModel.generateRebuttal(claim.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Generate AI Rebuttal")
                        }
                    } else {
                        Text(
                            "Generated Rebuttal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                claim.generatedRebuttal,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Button(
                            onClick = { viewModel.generateRebuttal(claim.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Regenerate Rebuttal")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSection(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

// Extra helper for putExtra (Antigravity instruction: check for mistakes)
fun Intent.putcharExtra(name: String, value: String?) {
    this.putExtra(name, value)
}
