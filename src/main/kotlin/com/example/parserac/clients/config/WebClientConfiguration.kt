package com.example.parserac.clients.config

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        val sslContext =
            SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build()
        val httpClient: HttpClient =
            HttpClient.create()
                .secure { t -> t.sslContext(sslContext) }

        return WebClient.builder()
            .filter(
                ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
                    println("Request: ${clientRequest.method()} ${clientRequest.url()}")
                    Mono.just(clientRequest)
                },
            )
            .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024) }
            .clientConnector(ReactorClientHttpConnector(httpClient))
    }
}
