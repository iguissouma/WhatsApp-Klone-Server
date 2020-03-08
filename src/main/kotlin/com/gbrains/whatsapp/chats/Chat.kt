package com.gbrains.whatsapp.chats

import com.expediagroup.graphql.annotations.GraphQLID
import com.gbrains.whatsapp.common.ConnectionDirective
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component

@Component(value = "chat")
@Scope("prototype")
class Chat @Autowired(required = false) constructor(@GraphQLID val id: String, private val currentUserId: String) {

    @Autowired
    private lateinit var chatsService: ChatsService

    @Autowired
    private lateinit var db: DatabaseClient

    suspend fun name(): String? {
        val participant = chatsService.firstRecipient(this.id, this.currentUserId)
        return participant?.name
    }

    suspend fun picture(): String? {
        val participant = chatsService.firstRecipient(this.id, currentUserId)
        // TODO("add unsplash api")
        return participant?.picture
    }


    @ConnectionDirective(key = "messages")
    suspend fun messages(limit: Int, after: Float?): MessagesResult {
        return chatsService.findMessagesByChat(
                chatId = id,
                limit = limit,
                after = after,
                userId = currentUserId
        )
    }

    suspend fun lastMessage(): Message? {
        return chatsService.lastMessage(chatId = id, userId = currentUserId)
    }

    suspend fun participants(): List<Message> {
        return chatsService.participants(chatId = id, userId = currentUserId)
    }
}

data class MessagesResult(val cursor: Float?, val hasMore: Boolean, val messages: List<Message>)

