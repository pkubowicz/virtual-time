package com.nexocode.virtualtime.subscribe

import org.apache.logging.log4j.LogManager
import java.util.concurrent.CopyOnWriteArrayList

class EmailSender {
    private val logger = LogManager.getLogger()

    val sent: MutableList<Email> = CopyOnWriteArrayList()

    fun send(email: Email) {
        sent.add(email)
        logger.info("Sent $email")
    }
}

data class Email(
    private val description: String,
)
