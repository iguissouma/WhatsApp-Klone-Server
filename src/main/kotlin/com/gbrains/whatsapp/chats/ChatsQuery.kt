package com.gbrains.whatsapp.chats

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.spring.operations.Query
import com.gbrains.whatsapp.common.MyGraphQLContext
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ChatsQuery(private val chatsService: ChatsService) : Query {

    //@PreAuthorize("isAuthenticated()")
    suspend fun chats(@GraphQLContext context: MyGraphQLContext): List<Chat> {
        val currentUserId = context.currentUser?.id ?: return listOf()
        return chatsService.findChatsByUser(currentUserId)
    }

    suspend fun chat(@GraphQLID chatId: String, @GraphQLContext context: MyGraphQLContext): Chat? {
        val currentUserId = context.currentUser?.id ?: return null
        return chatsService.findChatById(chatId, currentUserId)
    }

}

@Component("chatNameDataFetcher")
class ChatNameDataFetcher(private val chatsService: ChatsService) : DataFetcher<String> {

    override fun get(environment: DataFetchingEnvironment): String {
        val chatId = environment.getSource<Chat>()?.id
        return "name"
    }

}
