package com.gbrains.whatsapp.chats

import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.spring.operations.Mutation
import com.gbrains.whatsapp.common.MyGraphQLContext
import com.gbrains.whatsapp.common.PubSub
import org.springframework.stereotype.Component

@Component
class ChatsMutation(private val chatsService: ChatsService,
                    private val pubSub: PubSub) : Mutation {

    suspend fun addMessage(@GraphQLID chatId: String, content: String, context: MyGraphQLContext): Message? {
        val currentUserId = context.currentUser?.id ?: return null
        val messageAdded = chatsService.addMessage(chatId,
                currentUserId,
                content)
        this.pubSub.messageAddedSink.next(messageAdded)
        return messageAdded
    }

    suspend fun addChat(@GraphQLID recipientId: String, context: MyGraphQLContext): Chat? {
        val currentUserId = context.currentUser?.id ?: return null
        val chatAdded = chatsService.addChat(currentUserId, recipientId)
        this.pubSub.chatAddedSink.next(chatAdded)
        return chatAdded
    }

    suspend fun removeChat(@GraphQLID chatId: String, context: MyGraphQLContext): String? {
        val currentUserId = context.currentUser?.id ?: return null
        chatsService.removeChat(chatId, currentUserId)
        this.pubSub.chatRemovedSink.next(chatId)
        return chatId.toString()
    }

}
