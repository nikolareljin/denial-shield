package com.denialshield.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denialshield.data.model.DenialClaim
import com.denialshield.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onAddClaim: () -> Unit,
    onEditUserInfo: () -> Unit,
    onClaimClick: (Long) -> Unit
) {
    val claims by viewModel.allClaims.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DenialShield") },
                actions = {
                    IconButton(onClick = onEditUserInfo) {
                        Icon(Icons.Default.Person, contentDescription = "User Info")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClaim) {
                Icon(Icons.Default.Add, contentDescription = "Add Claim")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (userInfo == null || userInfo?.firstName?.isEmpty() == true) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Complete your profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Add your personal info to generate accurate rebuttals.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = onEditUserInfo,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Setup Profile")
                        }
                    }
                }
            }

            if (claims.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No claims yet. Tap + to start.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(claims) { claim ->
                        ClaimCard(claim = claim, onClick = { onClaimClick(claim.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ClaimCard(claim: DenialClaim, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    claim.providerName.ifBlank { "Unknown Provider" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    claim.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                "Claim ID: ${claim.claimId.ifBlank { "N/A" }}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Reason: ${claim.denialReasonDescription.ifBlank { "Pending intake" }}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}
