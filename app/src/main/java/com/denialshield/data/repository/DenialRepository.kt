package com.denialshield.data.repository

import com.denialshield.data.local.dao.DenialDao
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.Evidence
import com.denialshield.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

class DenialRepository(private val denialDao: DenialDao) {

    val userInfo: Flow<UserInfo?> = denialDao.getUserInfo()
    val allClaims: Flow<List<DenialClaim>> = denialDao.getAllClaims()

    suspend fun saveUserInfo(userInfo: UserInfo) {
        denialDao.insertUserInfo(userInfo)
    }

    suspend fun insertClaim(claim: DenialClaim): Long {
        return denialDao.insertClaim(claim)
    }

    suspend fun updateClaim(claim: DenialClaim) {
        denialDao.updateClaim(claim)
    }

    suspend fun getClaimById(id: Long): DenialClaim? {
        return denialDao.getClaimById(id)
    }

    suspend fun deleteClaim(id: Long) {
        denialDao.deleteClaim(id)
    }

    suspend fun addEvidence(evidence: Evidence) {
        denialDao.insertEvidence(evidence)
    }

    fun getEvidenceForClaim(claimId: Long): Flow<List<Evidence>> {
        return denialDao.getEvidenceForClaim(claimId)
    }
}
