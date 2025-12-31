package com.denialshield.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denialshield.data.model.DenialClaim
import com.denialshield.ui.viewmodel.MainViewModel
import com.denialshield.utils.PdfExporter
import kotlinx.coroutines.launch

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
    val userInfo by viewModel.userInfo.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Medical Rebuttal", claim.generatedRebuttal)
                            clipboardManager.setPrimaryClip(clip)
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Medical Appeal Rebuttal - ${claim.providerName}")
                                putExtra(Intent.EXTRA_TEXT, claim.generatedRebuttal)
                            }
                            context.startActivity(Intent.createChooser(intent, "Send Rebuttal"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        
                        IconButton(onClick = {
                            val user = userInfo
                            if (user != null) {
                                scope.launch {
                                    val uri = PdfExporter.exportToPdf(context, user, claim)
                                    if (uri != null) {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share PDF"))
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
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
