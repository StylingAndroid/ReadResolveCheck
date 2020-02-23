package com.stylingandroid.readresolve.lint

import com.android.tools.lint.client.api.IssueRegistry

@Suppress("UnstableApiUsage")
class LibraryIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        SerializableObjectMissingReadResolveDetector.MISSING_READ_RESOLVE_ISSUE
    )

    override val api = com.android.tools.lint.detector.api.CURRENT_API
}
