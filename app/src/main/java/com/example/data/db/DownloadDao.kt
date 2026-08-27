package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE statusName = 'COMPLETED' ORDER BY createdAt DESC")
    fun getCompletedMedia(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE statusName IN ('DOWNLOADING', 'QUEUED', 'PAUSED') ORDER BY createdAt DESC")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(download: DownloadEntity)

    @Update
    suspend fun update(download: DownloadEntity)

    @Delete
    suspend fun delete(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE downloads SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE downloads SET title = :newTitle WHERE id = :id")
    suspend fun updateTitle(id: String, newTitle: String)

    @Query("UPDATE downloads SET statusName = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM downloads WHERE statusName = 'COMPLETED'")
    suspend fun clearCompleted()
}
