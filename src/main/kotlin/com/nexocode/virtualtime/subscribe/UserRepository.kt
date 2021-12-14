package com.nexocode.virtualtime.subscribe

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

class UserRepository {
    private var managers: List<User> = emptyList()

    fun saveAll(users: Collection<User>): Flux<User> =
        Flux.fromIterable(users)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnComplete { managers = users.toList() }

    fun findAllManagers(): Flux<User> =
        FakeDatabase.delayedFlux(managers, Duration.ofMillis(500))
            .publishOn(Schedulers.boundedElastic())
}
