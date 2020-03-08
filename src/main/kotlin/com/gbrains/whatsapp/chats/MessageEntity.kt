package com.gbrains.whatsapp.chats

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("messages")
data class MessageEntity(
        @Id val id: Int? = null,
        val content: String,
        val createdAt: LocalDateTime,
        val chatId: Int,
        val senderUserId: Int
)
