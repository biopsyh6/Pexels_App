package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos WHERE type = :type AND timestamp > :expiration")
    suspend fun getCachedPhotos(type: String, expiration: Long): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE type = :type AND timestamp <= :expiration")
    suspend fun clearExpiredPhotos(type: String, expiration: Long)

    @Query("SELECT * FROM photos WHERE id = :id LIMIT 1")
    suspend fun getPhotoById(id: Int): PhotoEntity?

    @Query("UPDATE photos SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM photos WHERE isBookmarked = 1 ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getBookmarkedPhotos(offset: Int, limit: Int): List<PhotoEntity>
}