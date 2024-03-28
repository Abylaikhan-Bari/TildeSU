package com.ashim_bari.tildesu.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashim_bari.tildesu.data.db.dao.UserDao
import com.ashim_bari.tildesu.data.db.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}