package com.animalshelter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule


@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .findAndAddModules()
            .build()
    }
}