package com.nexocode.virtualtime.subscribe

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

class ApplicationRepository {
    fun save(application: Application): Mono<Application> =
        FakeDatabase.delayedMono(application.copy(id = "new-id"), Duration.ofMillis(400))
            .publishOn(Schedulers.boundedElastic())
}
