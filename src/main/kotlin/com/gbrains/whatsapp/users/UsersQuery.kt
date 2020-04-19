package com.gbrains.whatsapp.users

import com.expediagroup.graphql.spring.operations.Query
import com.gbrains.whatsapp.common.MyGraphQLContext
import org.springframework.stereotype.Component


@Component
class UsersQuery(private val userService: UserService) : Query {

    suspend fun users(context: MyGraphQLContext): List<User> {
        val currentUser = context.currentUser ?: return listOf<User>()
        return userService.findAllExcept(currentUser.id.toString())
    }

    suspend fun me(context: MyGraphQLContext): User? {
        //ReactiveSecurityContextHolder.getContext()
        val currentUser = context.currentUser ?: return null
        return userService.findById(userId = currentUser.id.toString())
    }

}
