package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE pair LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Delete
    suspend fun deleteScan(scan: ScanEntity)

    @Query("UPDATE scans SET status = :status WHERE id = :id")
    suspend fun updateScanStatus(id: Long, status: String)

    @Query("DELETE FROM scans")
    suspend fun clearAllScans()
}
