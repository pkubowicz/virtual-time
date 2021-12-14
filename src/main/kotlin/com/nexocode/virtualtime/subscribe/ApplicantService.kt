package com.nexocode.virtualtime.subscribe

import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono

class ApplicantService(
    private val applicationRepository: ApplicationRepository,
    private val emailSender: EmailSender,
    private val userRepository: UserRepository,
) {
    private val logger = LogManager.getLogger()

    fun submitApplication(application: Application): Mono<Application> {
        validate(application)
        userRepository.findAllManagers()
            .subscribe { manager -> emailSender.send(Email("to ${manager.email}")) }
        return applicationRepository.save(application)
            .doOnNext { logger.info("Saved application") }
    }

    private fun validate(application: Application) {
        logger.info("Validated")
    }
}
