package com.nexocode.virtualtime.delay

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.test.StepVerifier
import java.time.Duration

// each test uses a different file name
// failing tests will still run in background as next tests are run
// unique file names make it easier to read logs
class FileServiceTest {
    private val storageLogic = mock<StorageLogic>()
    private val fileService = FileService(FakeStorage(storageLogic))

    @Test
    fun `cleans up failed upload, when no delay`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.uploadNoDelay("/apple", "content").block()

        verify(storageLogic).delete("/apple")
    }

    @Test
    fun `cleans up failed upload, failing because of missing test wait`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.upload("/deodorant", "content").block()

        verify(storageLogic).delete("/deodorant")
    }

    @Test
    fun `cleans up failed upload, passing with Mockito timeout but too slow`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.upload("/doll", "content").block()

        verify(storageLogic, timeout(1000)).delete("/doll")
    }

    @Test
    fun `cleans up failed upload, failing with StepVerifier without virtual time`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        StepVerifier.create(fileService.upload("/glass", "content"))
            .thenAwait(Duration.ofMillis(1000)) // does not wait at all because stream is completed
            .verifyComplete()

        verify(storageLogic).delete("/glass")
    }

    @Test
    fun `passing Mockito despite too early delete`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.uploadNoDelay("/glue", "content").block()

        verify(storageLogic, timeout(1000)).delete("/glue")
    }

    @Test
    fun `failing because Mockito detects too early delete`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.uploadNoDelay("/banana", "content").block()

        verify(storageLogic, after(700).never()).delete("/banana")
        verify(storageLogic, timeout(300)).delete("/banana")
    }

    @Test
    fun `passing Mockito with detecting too early delete`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        fileService.upload("/tape", "content").block()

        verify(storageLogic, after(700).never()).delete("/tape")
        verify(storageLogic, timeout(300)).delete("/tape")
    }

    @Test
    fun `cleans up failed upload, failing because of too short test wait`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        StepVerifier.withVirtualTime { fileService.upload("/shirt", "content") }
            .thenAwait(Duration.ofMillis(400))
            .verifyComplete()

        verify(storageLogic).delete("/shirt")
    }

    @Test
    fun `cleans up failed upload, failing because of no delay`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        StepVerifier.withVirtualTime { fileService.uploadNoDelay("/button", "content") }
            .then { verify(storageLogic, never()).delete(any()) }
            .thenAwait(Duration.ofMillis(800))
            .verifyComplete()

        verify(storageLogic).delete("/button")
    }

    @Test
    fun `cleans up failed upload, with proper test wait`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        StepVerifier.withVirtualTime { fileService.upload("/soda", "content") }
            .then { verify(storageLogic, never()).delete(any()) }
            .thenAwait(Duration.ofMillis(800))
            .verifyComplete()

        verify(storageLogic).delete("/soda")
    }

    @Test
    fun `cleans up failed upload, also when scheduling on Scheduler`() {
        whenever(storageLogic.save(any(), any())).thenThrow(StorageException())
        StepVerifier.withVirtualTime { fileService.uploadWithScheduler("/charger", "content") }
            .then { verify(storageLogic, never()).delete(any()) }
            .thenAwait(Duration.ofMillis(800))
            .verifyComplete()

        verify(storageLogic).delete("/charger")
    }

    @Test
    fun `does not clean when upload succeeds`() {
        StepVerifier.withVirtualTime { fileService.upload("/can", "content") }
            .thenAwait(Duration.ofMillis(800))
            .verifyComplete()

        verify(storageLogic, never()).delete("/can")
    }

    @Test
    fun `does not clean when upload succeeds, failing when always deleted`() {
        StepVerifier.withVirtualTime { fileService.uploadAlwaysDelete("/balloon", "content") }
            .thenAwait(Duration.ofMillis(800))
            .verifyComplete()

        verify(storageLogic, never()).delete("/balloon")
    }
}
