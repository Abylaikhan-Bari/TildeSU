package com.ashim_bari.tildesu.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashim_bari.tildesu.db.dao.UserDao
import com.ashim_bari.tildesu.db.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // Define other Daos here if necessary
}