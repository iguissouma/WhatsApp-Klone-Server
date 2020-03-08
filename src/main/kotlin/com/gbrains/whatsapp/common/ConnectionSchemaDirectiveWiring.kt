package com.gbrains.whatsapp.common

import com.expediagroup.graphql.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import java.util.function.BiFunction

class ConnectionSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {

    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<Any> = environment.getDataFetcher()

        val connectionFetcher = DataFetcherFactories.wrapDataFetcher(
                originalDataFetcher,
                BiFunction<DataFetchingEnvironment, Any, Any> { _, value -> value.toString() }
        )
        environment.setDataFetcher(connectionFetcher)
        return field
    }
}