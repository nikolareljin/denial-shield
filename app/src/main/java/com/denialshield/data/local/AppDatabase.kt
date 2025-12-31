package com.denialshield.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.denialshield.data.local.dao.DenialDao
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.Evidence
import com.denialshield.data.model.UserInfo

@Database(entities = [UserInfo::class, DenialClaim::class, Evidence::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun denialDao(): DenialDao
}
