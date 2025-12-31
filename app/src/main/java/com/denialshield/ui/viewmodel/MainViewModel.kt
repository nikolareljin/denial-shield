package com.denialshield.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.Evidence
import com.denialshield.data.model.UserInfo
import com.denialshield.data.repository.DenialRepository
import com.denialshield.utils.DocumentProcessor
import com.denialshield.utils.RebuttalGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: DenialRepository,
    private val documentProcessor: DocumentProcessor,
    private val aiGenerator: AiRebuttalGenerator
) : ViewModel() {

    val userInfo: StateFlow<UserInfo?> = repository.userInfo.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val allClaims: StateFlow<List<DenialClaim>> = repository.allClaims.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun saveUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            repository.saveUserInfo(userInfo)
        }
    }

    fun addClaim(claim: DenialClaim, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertClaim(claim)
            onComplete(id)
        }
    }

    fun updateClaim(claim: DenialClaim) {
        viewModelScope.launch {
            repository.updateClaim(claim)
        }
    }

    fun processDocument(claimId: Long, uri: Uri, isPdf: Boolean) {
        viewModelScope.launch {
            _isProcessing.value = true
            val text = if (isPdf) {
                documentProcessor.processPdf(uri)
            } else {
                documentProcessor.processImage(uri)
            }
            
            val claim = repository.getClaimById(claimId)
            if (claim != null) {
                val policyLanguage = documentProcessor.extractPolicyLanguage(text)
                val updatedClaim = claim.copy(
                    rawOcrText = text,
                    policyLanguageCited = policyLanguage,
                    status = "EVIDENCE"
                )
                repository.updateClaim(updatedClaim)
                
                repository.addEvidence(Evidence(
                    claimId = claimId,
                    uri = uri.toString(),
                    fileName = uri.lastPathSegment ?: "document",
                    mimeType = if (isPdf) "application/pdf" else "image/*"
                ))
            }
            _isProcessing.value = false
        }
    }

    fun generateRebuttal(claimId: Long) {
        viewModelScope.launch {
            _isProcessing.value = true
            val claim = repository.getClaimById(claimId)
            val user = userInfo.value
            if (claim != null && user != null) {
                val rebuttal = aiGenerator.generateRebuttal(user, claim)
                repository.updateClaim(claim.copy(
                    generatedRebuttal = rebuttal,
                    status = "GENERATED"
                ))
            }
            _isProcessing.value = false
        }
    }
}
