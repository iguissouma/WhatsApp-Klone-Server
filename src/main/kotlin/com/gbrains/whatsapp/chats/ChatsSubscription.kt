package com.gbrains.whatsapp.chats

import com.expediagroup.graphql.spring.operations.Subscription
import com.gbrains.whatsapp.common.MyGraphQLContext
import com.gbrains.whatsapp.common.PubSub
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class ChatsSubscription(private val chatsService: ChatsService, private val pubSub: PubSub) : Subscription {

    suspend fun messageAdded(context: MyGraphQLContext): Flux<Message> {
        return Flux.from(pubSub.messageAdded.map { it.apply { it.currentUserId = context.currentUser?.id!! } }.filter { withFilter(context, it.chatId) })
    }

    suspend fun chatAdded(context: MyGraphQLContext): Flux<Chat> {
        return Flux.from(pubSub.chatAdded.filter { withFilter(context, it.id) })
    }

    fun chatRemoved(context: MyGraphQLContext): Flux<String> {
        return Flux.from(pubSub.chatRemoved.filter { withFilter(context, it) })
    }

    private fun withFilter(context: MyGraphQLContext, chatId: String): Boolean {
        return context.currentUser?.id != null && chatsService.isParticipant(chatId, context.currentUser.id)
    }

}
