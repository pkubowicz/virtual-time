package com.nexocode.virtualtime.stepverifier

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class StepVerifierExamples {

    @Test
    fun checkingInfiniteFlux() {
        val infinite: Flux<Int> = Flux.generate(
            { 100 },
            { state, sink -> sink.next(state); state + 1 }
        )

        StepVerifier.create(infinite)
            .expectNext(100, 101, 102, 103)
            .thenCancel()
            .verify()
    }
}
