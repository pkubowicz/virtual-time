package com.nexocode.virtualtime.delay

import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono

class FakeStorage(private val logic: StorageLogic) : Storage {
    private val logger = LogManager.getLogger()

    override fun save(path: String, data: String) =
        Mono.fromRunnable<Void> {
            logger.info("Saving $path")
            logic.save(path, data)
        }

    override fun delete(path: String) =
        Mono.fromRunnable<Void> { deleteSync(path) }

    override fun deleteSync(path: String) {
        logger.info("Removing $path")
        logic.delete(path)
    }
}

interface StorageLogic {
    fun save(path: String, data: String)

    fun delete(path: String)
}
