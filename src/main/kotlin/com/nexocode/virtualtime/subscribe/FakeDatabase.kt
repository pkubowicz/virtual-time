package com.nexocode.virtualtime.subscribe

import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.delayedExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS

object FakeDatabase {
    private val logger = LogManager.getLogger()

    fun <T> delayedMono(result: T, delay: Duration): Mono<T> =
        Mono.fromFuture(delayedFuture(result, delay))

    fun <T> delayedFlux(result: Iterable<T>, delay: Duration): Flux<T> =
        delayedMono(result, delay).flatMapIterable { it }

    private fun <T> delayedFuture(result: T, delay: Duration): CompletableFuture<T> =
        CompletableFuture.supplyAsync(
            { logger.info("Returning $result") ; result },
            delayedExecutor(delay.toMillis(), MILLISECONDS)
        )
}
