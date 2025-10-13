package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CollectionDao
import com.example.data.local.dao.PhotoDao
import com.example.data.local.entity.CollectionEntity
import com.example.data.local.entity.PhotoEntity

@Database(entities = [PhotoEntity::class, CollectionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun collectionDao(): CollectionDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pexels_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}