package com.gbrains.whatsapp.users

import com.expediagroup.graphql.spring.operations.Mutation
import com.gbrains.whatsapp.common.MyGraphQLContext
import com.gbrains.whatsapp.config.JWTUtil
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.isEquals
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UsersMutation(private val userRepository: UserRepository,
                    private val userService: UserService,
                    private val db: DatabaseClient,
                    private val jwtUtil: JWTUtil
) : Mutation {

    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(8)

    suspend fun signIn(username: String, password: String, context: MyGraphQLContext): User? {
        val entity: UserEntity = db.select()
                .from("users")
                .matching(where("username").isEquals(username))
                .`as`(UserEntity::class.java)
                .fetch()
                .one()
                .awaitFirstOrNull() ?: throw RuntimeException("user not found")

        if (!validPassword(password, entity.password)) {
            throw RuntimeException("password is incorrect")
        }

        // generate token
        val generateToken = jwtUtil.generateToken(com.gbrains.whatsapp.config.User(username, password, true, emptyList()))

        // create a cookie
        val cookie = ResponseCookie.from("authToken", generateToken)
                // .domain(this.cookieDomain)
                //.httpOnly(this.cookieHttpOnly)
                .maxAge(24 * 60 * 60 * 1000)
                //.path(Optional.ofNullable(this.cookiePath).orElseGet({ getRequestContext(exchange.getRequest()) }))
                //.secure(Optional.ofNullable(exchange.getRequest().getSslInfo()).map({ sslInfo -> true }).orElse(false))
                .build()
        //add cookie to response
        context.response.addCookie(cookie)

        return entity.toUser()
    }

    private fun validPassword(password: String, localPassword: String): Boolean {
        //return localPassword == passwordEncoder.encode(password)
        return passwordEncoder.matches(password, localPassword)
    }

    suspend fun signUp(
            name: String,
            username: String,
            password: String,
            passwordConfirm: String
    ): User? {

        validateLength("req.name", name, 3, 50)
        validateLength("req.username", username, 3, 18)
        validatePassword("req.password", password)

        if (password != passwordConfirm) {
            throw Error("req.password and req.passwordConfirm don't match")
        }

        val existingUser = userService.findByUsername(username)

        if (existingUser != null) {
            throw Error("username already exists")
        }

        return this.userService.newUser(
                username,
                name,
                password
        )


    }

    private fun validatePassword(ctx: String, str: String) {
        validateLength(ctx, str, 8, 30)

        if (!"[a-zA-Z]".toRegex().containsMatchIn(str)) {
            throw Error("$ctx must contain english letters")
        }

        if (!"\\d+".toRegex().containsMatchIn(str)) {
            throw Error("$ctx must contain numbers")
        }

        if (!"[^A-Za-z0-9]".toRegex().containsMatchIn(str)) {
            throw Error("$ctx must contain special charachters")
        }
    }

    private fun validateLength(ctx: String, str: String, min: Int, max: Int) {

        if (str.length < min) {
            throw Error("$ctx must be at least $min chars long")
        }

        if (str.length > max) {
            throw Error("$ctx must contain $max chars at most")
        }
    }

}

private fun UserEntity.toUser(): User {
    return User(this.id.toString(), this.username, this.name, this.picture)
}
