package com.denialshield.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.Evidence
import com.denialshield.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface DenialDao {
    @Query("SELECT * FROM user_info WHERE id = 1")
    fun getUserInfo(): Flow<UserInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInfo)

    @Query("SELECT * FROM denial_claims ORDER BY dateReceived DESC")
    fun getAllClaims(): Flow<List<DenialClaim>>

    @Query("SELECT * FROM denial_claims WHERE id = :id")
    suspend fun getClaimById(id: Long): DenialClaim?

    @Insert
    suspend fun insertClaim(claim: DenialClaim): Long

    @Update
    suspend fun updateClaim(claim: DenialClaim)

    @Query("DELETE FROM denial_claims WHERE id = :id")
    suspend fun deleteClaim(id: Long)

    @Insert
    suspend fun insertEvidence(evidence: Evidence)

    @Query("SELECT * FROM evidence_documents WHERE claimId = :claimId")
    fun getEvidenceForClaim(claimId: Long): Flow<List<Evidence>>
}
