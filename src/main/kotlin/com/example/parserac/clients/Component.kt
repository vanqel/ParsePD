package com.example.parserac.clients

import com.example.parserac.clients.dictionary.DictionaryClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.time.Duration

@Component
class Component(
    val webClient: DictionaryClient
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        Flux.fromIterable(10000000..100000000)
            .delayElements(java.time.Duration.ofMillis(80))
            .flatMap { webClient.compareInfo(it) }
            .parallel(10) // Set the desired parallelism value
            .runOn(Schedulers.parallel())
            .sequential() // Reset the parallelism to process the results sequentially
            .subscribe()
    }
}