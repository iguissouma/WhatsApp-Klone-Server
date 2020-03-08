package com.gbrains.whatsapp.common

import com.expediagroup.graphql.execution.SimpleKotlinDataFetcherFactoryProvider
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcherFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * Custom DataFetcherFactory provider that returns custom Spring based DataFetcherFactory for resolving lateinit properties.
 */
class CustomDataFetcherFactoryProvider(
        private val springDataFetcherFactory: SpringDataFetcherFactory,
        private val objectMapper: ObjectMapper
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper) {

    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>) = DataFetcherFactory {
        CustomFunctionDataFetcher(
                target = target,
                fn = kFunction,
                objectMapper = objectMapper)
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> =
            if (kProperty.isLateinit) {
                springDataFetcherFactory as DataFetcherFactory<Any?>
            } else {
                super.propertyDataFetcherFactory(kClass, kProperty)
            }
}