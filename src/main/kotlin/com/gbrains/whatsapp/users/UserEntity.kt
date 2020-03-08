package com.gbrains.whatsapp.users

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserEntity(
        @Id val id: Int? = null,
        val username: String,
        val name: String,
        val password: String,
        val picture: String
)