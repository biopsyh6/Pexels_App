package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CollectionEntity

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)

    @Query("SELECT * FROM collections WHERE timestamp > :expiration")
    suspend fun getCachedCollections(expiration: Long): List<CollectionEntity>

    @Query("DELETE FROM collections WHERE timestamp <= :expiration")
    suspend fun clearExpiredCollections(expiration: Long)

}