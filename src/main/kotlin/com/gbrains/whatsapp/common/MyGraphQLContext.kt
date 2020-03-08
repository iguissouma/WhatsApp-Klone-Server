package com.gbrains.whatsapp.common

import com.expediagroup.graphql.annotations.GraphQLContext
import com.gbrains.whatsapp.users.User
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse

/**
 * Simple [GraphQLContext] that holds extra value.
 */
class MyGraphQLContext(val currentUser: User?, val request: ServerHttpRequest, val response: ServerHttpResponse)