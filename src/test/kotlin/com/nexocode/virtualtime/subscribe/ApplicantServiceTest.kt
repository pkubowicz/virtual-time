package com.nexocode.virtualtime.subscribe

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.Duration

class ApplicantServiceTest {
    private val emailSender = EmailSender()
    private val userRepository = UserRepository()
    private val applicantService = ApplicantService(ApplicationRepository(), emailSender, userRepository)

    @Test
    fun `sends emails to managers, failing because no wait`() {
        userRepository.saveAll(listOf(User("mgr1@mail.com"), User("mgr2@mail.com"))).blockLast()

        applicantService.submitApplication(Application("joe@mail.com")).block()

        assertThat(emailSender.sent).containsExactlyInAnyOrder(
            Email("to mgr1@mail.com"), Email("to mgr2@mail.com")
        )
    }

    @Test
    fun `sends emails to managers, virtual time, still failing`() {
        userRepository.saveAll(listOf(User("mgr3@mail.com"), User("mgr4@mail.com"))).blockLast()

        StepVerifier.withVirtualTime { applicantService.submitApplication(Application("joe@mail.com")) }
            .thenAwait(Duration.ofMillis(1000))
            .expectNextCount(1)
            .verifyComplete()

        assertThat(emailSender.sent).containsExactlyInAnyOrder(
            Email("to mgr3@mail.com"), Email("to mgr4@mail.com")
        )
    }
}
