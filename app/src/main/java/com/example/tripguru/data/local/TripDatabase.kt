package com.example.tripguru.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tripguru.data.local.dao.TripDao
import com.example.tripguru.data.local.entity.TripEntity

@Database(
    entities = [TripEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}