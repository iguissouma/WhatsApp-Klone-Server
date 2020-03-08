package com.gbrains.whatsapp.common

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation.FIELD
import graphql.introspection.Introspection.DirectiveLocation.FIELD_DEFINITION

@GraphQLDirective(
        name = "connection",
        description = "Specify a custom store key for this result. See\n" +
                "https://www.apollographql.com/docs/react/advanced/caching/#the-connection-directive",
        locations = [FIELD_DEFINITION, FIELD]

)
annotation class ConnectionDirective(val key: String)


