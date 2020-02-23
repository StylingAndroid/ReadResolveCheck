package com.stylingandroid.readresolve.lint

import com.android.tools.lint.detector.api.CURRENT_API
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class LibraryIssueRegistryTest {
    private val libraryIssueRegistry = LibraryIssueRegistry()

    @Test
    fun correctIssueIsRegistered() {
        assertTrue(libraryIssueRegistry.issues.contains(SerializableObjectMissingReadResolveDetector.MISSING_READ_RESOLVE_ISSUE))
    }

    @Test
    fun onlyASingleIssueIsRegistered() {
        assertEquals(libraryIssueRegistry.issues.size, 1)
    }

    @Test
    fun isCorrectApiLevel() {
        assertEquals(libraryIssueRegistry.api, CURRENT_API)
    }
}
