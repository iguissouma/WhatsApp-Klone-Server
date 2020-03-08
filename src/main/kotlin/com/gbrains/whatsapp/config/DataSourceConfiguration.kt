package com.gbrains.whatsapp.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class DataSourceConfiguration {

    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties("spring.datasource")
    fun dataSource(properties: DataSourceProperties): DataSource = DataSourceBuilder.create()
            .driverClassName("org.postgresql.Driver")
            .username(properties.dataUsername)
            .password(properties.dataPassword)
            .url(properties.url)
            .build()
}