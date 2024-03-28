package com.ashim_bari.tildesu.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ashim_bari.tildesu.data.db.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserProfile(userId: String): UserEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    @Update
    suspend fun updateUserProfile(user: UserEntity)
}