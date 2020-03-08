package com.gbrains.whatsapp.common

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveEnvironment
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import graphql.schema.GraphQLDirectiveContainer
import kotlin.reflect.KClass

class CustomDirectiveWiringFactory : KotlinDirectiveWiringFactory(manualWiring = mapOf<String, KotlinSchemaDirectiveWiring>()) {

    private val connectionDirectiveWiring = ConnectionSchemaDirectiveWiring()

    override fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? = when {
        environment.directive.name == getDirectiveName(ConnectionDirective::class) -> connectionDirectiveWiring
        else -> null
    }
}

internal fun getDirectiveName(kClass: KClass<out Annotation>): String = kClass.simpleName!!.decapitalize()