package com.gbrains.whatsapp.chats

import com.expediagroup.graphql.annotations.GraphQLID
import com.gbrains.whatsapp.users.User
import com.gbrains.whatsapp.users.UserEntity
import com.gbrains.whatsapp.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component(value = "message")
@Scope("prototype")
data class Message @Autowired(required = false)
constructor(@GraphQLID val id: String,
            val chatId: String,
            val createdAt: LocalDateTime,
            val content: String,
            private val senderUserId: String,
            internal var currentUserId: String

) {

    @Autowired
    private lateinit var chatsService: ChatsService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var db: DatabaseClient

    suspend fun chat(): Chat? = chatsService.findChatById(chatId, currentUserId)

    fun isMine(): Boolean {
        return this.senderUserId == currentUserId
    }

    suspend fun recipient(): UserEntity? = chatsService.firstRecipient(chatId, senderUserId)

    suspend fun sender(): User? = userService.findById(senderUserId)

}
