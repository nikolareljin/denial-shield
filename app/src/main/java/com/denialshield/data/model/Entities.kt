package com.denialshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfo(
    @PrimaryKey val id: Int = 1, // Only one user info record
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val insuranceName: String = "",
    val policyNumber: String = ""
)

@Entity(tableName = "denial_claims")
data class DenialClaim(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateReceived: Long = System.currentTimeMillis(),
    val providerName: String = "",
    val claimId: String = "",
    val denialReasonCode: String = "",
    val denialReasonDescription: String = "",
    val policyLanguageCited: String = "",
    val rawOcrText: String = "",
    val status: String = "INTAKE", // INTAKE, EVIDENCE, GENERATED, SENT
    val generatedRebuttal: String = ""
)

@Entity(tableName = "evidence_documents")
data class Evidence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val claimId: Long,
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val timestamp: Long = System.currentTimeMillis()
)
