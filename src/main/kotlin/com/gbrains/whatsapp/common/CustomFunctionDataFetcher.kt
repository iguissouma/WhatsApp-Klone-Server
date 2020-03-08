package com.gbrains.whatsapp.common
import com.expediagroup.graphql.execution.FunctionDataFetcher
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetchingEnvironment
import reactor.core.publisher.Mono
import kotlin.reflect.KFunction

/**
 * Custom function data fetcher that adds support for Reactor Mono.
 */
class CustomFunctionDataFetcher(target: Any?, fn: KFunction<*>, objectMapper: ObjectMapper) : FunctionDataFetcher(target, fn, objectMapper) {

    override fun get(environment: DataFetchingEnvironment): Any? = when (val result = super.get(environment)) {
        is Mono<*> -> result.toFuture()
        else -> result
    }
}