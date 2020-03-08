package com.gbrains.whatsapp.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository : ServerSecurityContextRepository {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request = swe.request
        val authToken = request.cookies.getFirst("authToken")?.value

        if (authToken != null) {
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            val map: Mono<SecurityContext> = this.authenticationManager!!.authenticate(auth).map { SecurityContextImpl(it) }
            return map
        } else {
            return Mono.empty()
        }
    }

}