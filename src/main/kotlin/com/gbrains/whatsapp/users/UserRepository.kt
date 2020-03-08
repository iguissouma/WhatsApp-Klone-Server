package com.gbrains.whatsapp.users

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.r2dbc.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<UserEntity, Int> {

    @Query("SELECT * FROM users WHERE username = $1")
    fun findByUsername(username: String): Mono<User>
}
