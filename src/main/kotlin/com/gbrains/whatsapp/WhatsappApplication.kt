package com.gbrains.whatsapp

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.fasterxml.jackson.databind.ObjectMapper
import com.gbrains.whatsapp.common.CustomDataFetcherFactoryProvider
import com.gbrains.whatsapp.common.CustomDirectiveWiringFactory
import com.gbrains.whatsapp.common.CustomSchemaGeneratorHooks
import com.gbrains.whatsapp.common.SpringDataFetcherFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.validation.Validator


@SpringBootApplication
class WhatsappApplication {

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(wiringFactory: KotlinDirectiveWiringFactory) = CustomSchemaGeneratorHooks(wiringFactory)

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, objectMapper: ObjectMapper) =
            CustomDataFetcherFactoryProvider(springDataFetcherFactory, objectMapper)


}

fun main(args: Array<String>) {
    runApplication<WhatsappApplication>(*args)
}
