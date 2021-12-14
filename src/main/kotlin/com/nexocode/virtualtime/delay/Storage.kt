package com.nexocode.virtualtime.delay

import reactor.core.publisher.Mono
import java.lang.RuntimeException

interface Storage {

    fun save(path: String, data: String): Mono<Void>

    fun delete(path: String): Mono<Void>

    // just to be able to test a variant with Scheduler.schedule()
    // 'normal' API won't have a method like this
    fun deleteSync(path: String)
}

class StorageException : RuntimeException()
