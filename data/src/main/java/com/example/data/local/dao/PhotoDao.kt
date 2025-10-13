package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PhotoEntity

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos WHERE type = :type AND timestamp > :expiration")
    fun getCachedPhotos(type: String, expiration: Long): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE type = :type AND timestamp <= :expiration")
    suspend fun clearExpiredPhotos(type: String, expiration: Long)

    @Query("SELECT * FROM photos WHERE id = :id LIMIT 1")
    suspend fun getPhotoById(id: Int): PhotoEntity?
}