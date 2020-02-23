package com.stylingandroid.readresolve.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
internal class SerializableObjectMissingReadResolveDetectorTest : LintDetectorTest() {

    override fun getDetector() =
        SerializableObjectMissingReadResolveDetector()
    override fun getIssues() = listOf(SerializableObjectMissingReadResolveDetector.MISSING_READ_RESOLVE_ISSUE)

    @Nested
    @DisplayName("Given a Kotlin Serializable object with readResolve")
    inner class SerializableKotlinObjectWithReadResolve {

        private val source = kotlin(
            """
            object MyObj : Serializable { override fun readResolve(): Any = MyObj }
            """.trimIndent()
        )

        @Test
        fun runLintCheckIsClean() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectClean()
        }
    }

    @Nested
    @DisplayName("Given a Kotlin Serializable object with readResolve which has arguments")
    inner class SerializableKotlinObjectWithReadResolveWithArguments {

        private val source = kotlin(
            """
            object MyObj : Serializable { 
            fun readResolve(any: Any): Any = MyObj 
            }
            """.trimIndent()
        )

        private val fixed = kotlin(
            """
            object MyObj : Serializable { 
            @Suppress("UnusedPrivateMember")
            private fun readResolve(): Any = MyObj 
            }
            """.trimIndent()
        )

        @Test
        fun runLintCheckHasSingleError() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectErrorCount(1)
        }

        @Test
        fun runLintFix() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .checkFix(null, fixed)
        }
    }

    @Nested
    @DisplayName("Given a Kotlin Serializable object with readResolve that lacks return type")
    inner class SerializableKotlinObjectWithReadResolveWithoutAnyReturnType {

        private val source = kotlin(
            """
            object MyObj : Serializable { 
            fun readResolve() = MyObj 
            }
            """.trimIndent()
        )

        private val fixed = kotlin(
            """
            object MyObj : Serializable { 
            @Suppress("UnusedPrivateMember")
            private fun readResolve(): Any = MyObj 
            }
            """.trimIndent()
        )

        @Test
        fun runLintCheckHasTwoError() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectErrorCount(1)
        }

        @Test
        fun runLintFix() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .checkFix(null, fixed)
        }
    }

    @Nested
    @DisplayName("Given a Kotlin Serializable object without readResolve")
    inner class SerializableKotlinObject {

        private val source = kotlin(
            """
            object MyObj : Serializable
            """.trimIndent()
        )

        private val fixed = kotlin(
            """
            object MyObj : Serializable {
            @Suppress("UnusedPrivateMember")
            private fun readResolve(): Any = MyObj
            }
            """.trimIndent()
        )

        @Test
        fun runLintCheckHasSingleError() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectErrorCount(1)
        }

        @Test
        fun runLintFix() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .checkFix(null, fixed)
        }
    }

    @Nested
    @DisplayName("Given a Kotlin Serializable object with a class body but no readResolve")
    inner class SerializableKotlinObjectWithClassBody {

        private val source = kotlin(
            """
            object MyObj : Serializable {}
            """.trimIndent()
        )

        private val fixed = kotlin(
            """
            object MyObj : Serializable {
            @Suppress("UnusedPrivateMember")
            private fun readResolve(): Any = MyObj
            }
            """.trimIndent()
        )

        @Test
        fun runLintCheckHasSingleError() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectErrorCount(1)
        }

        @Test
        fun runLintFix() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .checkFix(null, fixed)
        }
    }

    @Nested
    @DisplayName("Given a Kotlin non-Serializable object without readResolve")
    inner class NonSerializableKotlinObject {

        private val source = kotlin(
            """
            object MyObj
            """.trimIndent()
        )

        @Test
        fun runLintCheckIsClean() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectClean()
        }
    }

    @Nested
    @DisplayName("Given a Kotlin Serializable class")
    inner

    class SerializableKotlinClass {

        private val source = kotlin(
            """
            class MyClass : Serializable
            """.trimIndent()
        )

        @Test
        fun runLintCheckIsClean() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectClean()
        }
    }

    @Nested
    @DisplayName("Given a Java Serializable class without readResolve")
    inner

    class SerializableJavaClass {

        private val source = java(
            """
                package com.stylingandroid.serializableobject;
                class MyObj implements Serializable {}
            """.trimIndent()
        )

        @Test
        fun runLintCheckIsClean() {
            lint().files(source)
                .allowMissingSdk()
                .run()
                .expectClean()
        }
    }
}
