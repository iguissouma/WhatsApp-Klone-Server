package com.gbrains.whatsapp.common

import com.gbrains.whatsapp.chats.Chat
import com.gbrains.whatsapp.chats.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.DirectProcessor

@Component
class PubSub {

    final val messageAdded = DirectProcessor.create<Message>().serialize()
    val messageAddedSink = messageAdded.sink()
    final val chatAdded = DirectProcessor.create<Chat>().serialize()
    val chatAddedSink = chatAdded.sink()
    final val chatRemoved = DirectProcessor.create<String>().serialize()
    val chatRemovedSink = chatRemoved.sink()

}