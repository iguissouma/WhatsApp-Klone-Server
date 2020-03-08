package com.gbrains.whatsapp.chats

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("chats")
data class ChatEntity(
        @Id val id: Int? = null
)