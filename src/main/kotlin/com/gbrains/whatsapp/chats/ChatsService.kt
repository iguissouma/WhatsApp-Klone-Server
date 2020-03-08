package com.gbrains.whatsapp.chats

import com.gbrains.whatsapp.users.UserEntity
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ChatsService(private val db: DatabaseClient,
                   private val chatsRepository: ChatsRepository,
                   private val messageRepository: MessageRepository) : BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    suspend fun findChatsByUser(userId: String): List<Chat> {
        return this.db.execute("""
        SELECT chats.* FROM chats, chats_users
        WHERE chats.id = chats_users.chat_id
        AND chats_users.user_id = :userId
        """.trimIndent()
        ).bind("userId", userId.toInt())
                .`as`(ChatEntity::class.java)
                .fetch()
                .all()
                .map { beanFactory.getBean("chat", it.id.toString(), userId) as Chat }
                .asFlow()
                .toList()
    }

    suspend fun firstRecipient(chatId: String, userId: String): UserEntity? {
        return this.db.execute("""
        SELECT users.* FROM users, chats_users
        WHERE users.id != :userId
        AND users.id = chats_users.user_id
        AND chats_users.chat_id = :chatId
        """.trimIndent()
        ).bind("userId", userId.toInt())
                .bind("chatId", chatId.toInt())
                .`as`(UserEntity::class.java)
                .fetch()
                .one()
                .awaitFirstOrNull()

    }

    suspend fun findMessagesByChat(chatId: String, limit: Int, after: Float?, userId: String): MessagesResult {
        val query: StringBuilder = StringBuilder("SELECT * FROM messages")
        query.append(" WHERE chat_id = :chatId")

        if (after != null && after != 0.toFloat()) {
            // the created_at is the cursor
            query.append(" AND created_at < '${cursorToDate(after)}'")
        }

        query.append(" ORDER BY created_at DESC LIMIT $limit")

        var messages = this.db.execute(query.toString()
        ).bind("chatId", chatId.toInt())
                .`as`(MessageEntity::class.java)
                .bind("chatId", chatId.toInt())
                .fetch()
                .all()
                //.limitRequest(limit.toLong())
                .map { message(it, userId) }
                .asFlow()
                .toList()

        if (messages.isNullOrEmpty()) {
            return MessagesResult(
                    hasMore = false,
                    cursor = null,
                    messages = emptyList()
            )
        }

        // so we send them as old -> new
        messages = messages.reversed()

        // cursor is a number representation of created_at
        val cursor = if (messages.isNotEmpty()) Date().time else 0
        val next = this.db.execute(
                "SELECT * FROM messages WHERE chat_id = :chatId AND created_at < '${cursorToDate(cursor.toFloat())}' ORDER BY created_at DESC LIMIT $limit"
        ).`as`(MessageEntity::class.java)
                .bind("chatId", chatId.toInt())
                .fetch()
                .all()
                //.limitRequest(limit.toLong())
                .map { message(it, userId) }
                .asFlow()
                .toList()



        return MessagesResult(hasMore = next.size == 1,// means there's no more messages
                cursor = cursor.toFloat(),
                messages = messages)
    }

    fun message(it: MessageEntity, userId: String): Message {
        return beanFactory.getBean("message", it.id.toString(), it.chatId.toString(), it.createdAt, it.content, it.senderUserId.toString(), userId) as Message
    }

    private fun cursorToDate(after: Float): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(after.toLong()), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_DATE)
    }

    suspend fun lastMessage(chatId: String, userId: String): Message? {
        return this.db.execute("""
          SELECT * FROM messages 
      WHERE chat_id = :chatId
      ORDER BY created_at DESC 
      LIMIT 1
        """.trimIndent()
        )
                .bind("chatId", chatId.toInt())
                .`as`(MessageEntity::class.java)
                .fetch()
                .one()
                .map { message(it, userId) }
                .awaitFirstOrNull()
    }

    suspend fun participants(chatId: String, userId: String): List<Message> {
        return this.db.execute("""
         SELECT users.* FROM users, chats_users
      WHERE chats_users.chat_id = :chatId
      AND chats_users.user_id = users.id
        """.trimIndent()
        ).bind("chatId", chatId.toInt())
                .`as`(MessageEntity::class.java)
                .fetch()
                .all()
                .map { message(it, userId) }
                .asFlow()
                .toList()
    }


    fun isParticipant(chatId: String, userId: String): Boolean {
        return runBlocking {
            db.execute("""
           SELECT * FROM chats_users
       WHERE chat_id = :chatId
       AND user_id = :userId
         """.trimIndent()
            ).bind("chatId", chatId.toInt())
                    .bind("userId", userId.toInt())
                    //.`as`(MessageEntity::class.java)
                    .fetch()
                    .all()
                    .asFlow()
                    .toList()
                    .isNotEmpty()
        }

    }

    suspend fun findChatById(chatId: String, currentUserId: String): Chat? {
        return this.db.execute("""
          SELECT * FROM chats WHERE id = :chatId
        """.trimIndent()
        ).bind("chatId", chatId.toInt())
                .`as`(ChatEntity::class.java)
                .fetch()
                .one()
                .map { beanFactory.getBean("chat", chatId, currentUserId) as Chat }
                .awaitFirstOrNull()
    }

    suspend fun addMessage(chatId: String, userId: String, content: String): Message {
        return messageRepository
                .save(MessageEntity(id = null, content = content, createdAt = LocalDateTime.now(), chatId = chatId.toInt(), senderUserId = userId.toInt()))
                .map { message(it, userId) }
                .awaitFirst()

    }

    suspend fun addChat(userId: String, recipientId: String): Chat {

        val awaitFirstOrNull = db.execute("""
        SELECT chats.*
        FROM chats,
             (SELECT * FROM chats_users WHERE user_id = :userId) as chats_of_current_user,
             chats_users
        WHERE chats_users.chat_id = chats_of_current_user.chat_id
          AND chats.id = chats_users.chat_id
          AND chats_users.user_id = :recipientId
        """.trimIndent())
                .bind("userId", userId.toInt())
                .bind("recipientId", recipientId.toInt())
                .fetch()
                .one()
                .awaitFirstOrNull()

        // If there is already a chat between these two users, return it
        if (awaitFirstOrNull != null) {
            return beanFactory.getBean("chat", awaitFirstOrNull["id"].toString(), userId) as Chat
        }

        //val chatAdded = chatsRepository.save(ChatEntity(id = null)).awaitFirst()

        val chatAdded = db.execute("INSERT INTO chats\n" +
                "        DEFAULT VALUES\n" +
                "        RETURNING *")
                .map { row -> ChatEntity(id = row.get("id") as Int) }
                .awaitOne()


        db.execute("""
            INSERT INTO chats_users(chat_id, user_id)
            VALUES(${chatAdded.id}, ${userId.toInt()})
        """.trimIndent())
                .fetch()
                .rowsUpdated()
                .awaitFirst()

        db.execute("""
            INSERT INTO chats_users(chat_id, user_id)
        VALUES(${chatAdded.id}, ${recipientId.toInt()});
        """.trimIndent())
                .fetch()
                .rowsUpdated()
                .awaitFirst()

        return beanFactory.getBean("chat", chatAdded.id, userId) as Chat

    }

    suspend fun removeChat(chatId: String, userId: String) {
        db.execute("""
        SELECT chats.* FROM chats, chats_users
            WHERE id = :chatId
            AND chats.id = chats_users.chat_id
            AND chats_users.user_id = :userId
        """.trimIndent())
                .bind("chatId", chatId.toInt())
                .bind("userId", userId.toInt())
                .fetch()
                .one()
                .awaitFirst()


        db.execute("""
        DELETE FROM chats WHERE chats.id = :chatId
        """.trimIndent())
                .bind("chatId", chatId.toInt())
                .fetch()
                .rowsUpdated()
                .awaitFirst()


    }

}

