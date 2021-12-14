package com.nexocode.virtualtime.delay

import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.onErrorResume
import java.time.Duration
import java.util.concurrent.TimeUnit.MILLISECONDS

class FileService(private val storage: Storage) {
    private val logger = LogManager.getLogger()

    fun uploadNoDelay(path: String, data: String): Mono<Void> =
        storage.save(path, data)
            .onErrorResume(StorageException::class) {
                logger.info("Cleaning up $path after error")
                storage.delete(path)
            }

    fun upload(path: String, data: String): Mono<Void> =
        storage.save(path, data)
            .onErrorResume(StorageException::class) {
                logger.info("Scheduling $path cleanup after error")
                scheduleDelete(path)
                Mono.empty()
            }

    fun uploadAlwaysDelete(path: String, data: String): Mono<Void> {
        scheduleDelete(path)
        return storage.save(path, data)
    }

    fun uploadWithScheduler(path: String, data: String): Mono<Void> =
        storage.save(path, data)
            .onErrorResume(StorageException::class) {
                logger.info("Scheduling $path cleanup after error")
                scheduleDeleteOnScheduler(path)
                Mono.empty()
            }

    private fun scheduleDelete(path: String) {
        Mono.delay(deleteDelay)
            .then(storage.delete(path))
            .subscribe(
                { logger.info("Cleaned up $path") },
                { error -> logger.error("Failed to clean up $path", error) }
            )
    }

    private fun scheduleDeleteOnScheduler(path: String) {
        Schedulers.parallel().schedule({ storage.deleteSync(path) }, deleteDelay.toMillis(), MILLISECONDS)
    }

    companion object {
        private val deleteDelay = Duration.ofMillis(800)
    }
}
