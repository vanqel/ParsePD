package com.example.parserac.clients.dictionary.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class DirectionaryClientConfiguration(
    val httpClient: WebClient.Builder,
) {
    @Bean(name = ["WebClientNSI"])
    fun webClient(): WebClient = httpClient.baseUrl("http://www.set-5723.znaet.ru/").build()
}
