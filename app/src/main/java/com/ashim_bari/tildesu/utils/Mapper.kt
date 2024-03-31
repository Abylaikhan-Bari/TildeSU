package com.ashim_bari.tildesu.utils

import com.ashim_bari.tildesu.data.db.entities.UserEntity
import com.ashim_bari.tildesu.model.user.UserProfile

class Mapper {
    companion object {
        // Convert UserEntity to UserProfile
        fun UserEntity.toUserProfile() = UserProfile(
            email = this.email,
            name = this.name,
            surname = this.surname,
            city = this.city,
            age = this.age,
            gender = this.gender,
            specialty = this.specialty
        )

        // Convert UserProfile to UserEntity
        fun UserProfile.toUserEntity(userId: String) = UserEntity(
            uid = userId,
            email = this.email,
            name = this.name,
            surname = this.surname,
            city = this.city,
            age = this.age,
            gender = this.gender ?: 0, // Replace with default values if necessary
            specialty = this.specialty
        )
    }
}