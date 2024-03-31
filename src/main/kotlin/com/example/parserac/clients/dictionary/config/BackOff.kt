package com.example.parserac.clients.dictionary.config

import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

fun <T> Mono<T>.requestBackOff() =
    this.retryWhen(
        Retry.backoff(5, Duration.ofMillis(100)).filter {
                error ->
            error.message?.let { "Connection".contains(it) } ?: throw error
        },
    )
