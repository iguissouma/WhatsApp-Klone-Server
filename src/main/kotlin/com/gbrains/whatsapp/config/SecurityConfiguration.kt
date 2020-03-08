package com.gbrains.whatsapp.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val securityContextRepository: SecurityContextRepository? = null

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH")
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.allowCredentials = true
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .exceptionHandling()
                .authenticationEntryPoint { swe, e -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED } }.accessDeniedHandler { swe, e -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN } }.and()
                .csrf().disable()
                //.cors().and()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository!!)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/graphql").permitAll()
                .pathMatchers("/subscriptions").permitAll()
                .pathMatchers("/playground").permitAll()
                .anyExchange().authenticated()
                .and().build()
    }


}