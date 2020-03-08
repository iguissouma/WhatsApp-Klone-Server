package com.gbrains.whatsapp.users

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

const val DEFAULT_PROFILE_PIC = "https://raw.githubusercontent.com/Urigo/WhatsApp-Clone-Client-React/legacy/public/assets/default-profile-pic.jpg"


@Service
class UserService(val db: DatabaseClient, val userRepository: UserRepository) {

    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(8)

    suspend fun findAllExcept(userId: String): List<User> {
        return db.execute(
                """SELECT t.*
                FROM users t
                WHERE id != :userId
                """.trimIndent()
        ).bind("userId", userId.toInt())
                .`as`(UserEntity::class.java)
                .fetch()
                .all()
                .map { User(it.id.toString(), it.username, it.name, it.picture) }
                .asFlow()
                .toList()

    }

    suspend fun findById(userId: String): User? {
        return db.execute(
                """SELECT t.*
                FROM users t
                WHERE id = :userId
                """.trimIndent()
        ).bind("userId", userId.toInt())
                .`as`(UserEntity::class.java)
                .fetch()
                .one()
                .map { User(it.id.toString(), it.username, it.name, it.picture) }
                .awaitFirstOrNull()
    }

    suspend fun currentUser(): Mono<User> {
        return ReactiveSecurityContextHolder.getContext()
                .map { it.authentication }
                .map { it.principal }
                .flatMap { Mono.just(User("1", it as String, "ee", "eee")) }

    }

    suspend fun findByUsername(username: String): User? {
        return db.execute(
                """SELECT t.*
                FROM users t
                WHERE username = :username
                """.trimIndent()
        ).bind("username", username)
                .`as`(UserEntity::class.java)
                .fetch()
                .one()
                .map { User(it.id.toString(), it.username, it.name, it.picture) }
                .awaitFirstOrNull()
    }

    suspend fun newUser(username: String, name: String, password: String): User? {
        val passwordHash = passwordEncoder.encode(password)
        return userRepository.save(UserEntity(username = username, name = name, password = passwordHash, picture = DEFAULT_PROFILE_PIC))
                .map { User(it.id.toString(), it.username, it.name, it.picture) }
                .awaitFirstOrNull()
    }
}