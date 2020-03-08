package com.gbrains.whatsapp.common

import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.gbrains.whatsapp.config.JWTUtil
import com.gbrains.whatsapp.users.User
import com.gbrains.whatsapp.users.UserService
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

/**
 * [GraphQLContextFactory] that generates [MyGraphQLContext] that will be available when processing GraphQL requests.
 */
@Component
class MyGraphQLContextFactory(private val jwtUtil: JWTUtil,
                              private val userService: UserService
) : GraphQLContextFactory<MyGraphQLContext> {

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): MyGraphQLContext {
        var usernameFromToken: String? = null
        val authTokenCookie = request.cookies.getFirst("authToken")?.value
        var findByUsername: User? = null
        if (authTokenCookie != null && jwtUtil.validateToken(authTokenCookie)) {
            usernameFromToken = jwtUtil.getUsernameFromToken(authTokenCookie)
            findByUsername = userService.findByUsername(usernameFromToken)
        }

        return MyGraphQLContext(currentUser = findByUsername,
                request = request,
                response = response)
    }
}