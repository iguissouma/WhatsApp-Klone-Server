package com.gbrains.whatsapp.common

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.validation.Validator
import kotlin.reflect.KType

/**
 * Schema generator hook that adds additional scalar types.
 */
class CustomSchemaGeneratorHooks(override val wiringFactory: KotlinDirectiveWiringFactory) : SchemaGeneratorHooks {

    /**
     * Register additional GraphQL scalar types.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        LocalDateTime::class -> graphqlDateTimeType
        else -> null
    }

    /**
     * Register Reactor Mono monad type.
     */
    override fun willResolveMonad(type: KType): KType = when (type.classifier) {
        Mono::class -> type.arguments.firstOrNull()?.type
        else -> type
    } ?: type

}

internal val graphqlUUIDType = GraphQLScalarType.newScalar()
        .name("UUID")
        .description("A type representing a formatted java.util.UUID")
        .coercing(UUIDCoercing)
        .build()

internal val graphqlDateTimeType = GraphQLScalarType.newScalar()
        .name("DateTime")
        .description("A type representing a formatted java.util.DateTime")
        .coercing(DateCoercing)
        .build()

private object UUIDCoercing : Coercing<UUID, String> {
    override fun parseValue(input: Any?): UUID = UUID.fromString(
            serialize(
                    input
            )
    )

    override fun parseLiteral(input: Any?): UUID? {
        val uuidString = (input as? StringValue)?.value
        return UUID.fromString(uuidString)
    }

    override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
}

private object DateCoercing : Coercing<LocalDateTime, String> {
    override fun parseValue(input: Any?): LocalDateTime {
        return LocalDateTime.parse(serialize(input), DateTimeFormatter.ISO_DATE_TIME).truncatedTo(ChronoUnit.SECONDS)
    }

    override fun parseLiteral(input: Any?): LocalDateTime? {
        val dateString = (input as? StringValue)?.value
        return LocalDateTime.parse(dateString).truncatedTo(ChronoUnit.SECONDS)
    }

    override fun serialize(dataFetcherResult: Any?): String {
        return DateTimeFormatter.ISO_DATE_TIME.format((dataFetcherResult as LocalDateTime).truncatedTo(ChronoUnit.SECONDS))
    }
}