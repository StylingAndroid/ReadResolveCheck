package com.stylingandroid.readresolve.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiElement
import java.io.Serializable
import java.util.EnumSet
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.KotlinUClass
import org.jetbrains.uast.kotlin.declarations.KotlinUMethod

@Suppress("UnstableApiUsage")
internal class SerializableObjectMissingReadResolveDetector : Detector(), Detector.UastScanner {

    companion object {
        private const val MISSING_READ_RESOLVE_ISSUE_ID = "SerializableObjectWithoutReadResolve"
        private const val MISSING_READ_RESOLVE_ISSUE_DESCRIPTION =
            "Kotlin objects which implement Serializable should override `readResolve()` to " +
                    "maintain the correct behaviour of a Kotlin object"
        private const val MISSING_READ_RESOLVE_ISSUE_EXPLANATION =
            "Kotlin objects are singletons. Java serialization does not understand the singleton " +
                    "pattern so we must ensure that the singleton behaviour survives serialisation " +
                    "by implementing a `readResolve()` method which returns the singleton instance. " +
                    "e.g.\nobject MyObj: Serializable {\n\tprivate fun readResolve() {\n\t\t" +
                    "return MyObj\n\t}\n\nFailure to do this will mean that the object will not " +
                    "behave as Kotlin objects are supposed to."
        private const val INCORRECT_READ_RESOLVE_SIGNATURE_EXPLANATION =
            MISSING_READ_RESOLVE_ISSUE_EXPLANATION
        private val ISSUE_CATEGORY = Category.CORRECTNESS
        private const val ISSUE_PRIORITY = 1
        private val ISSUE_SEVERITY = Severity.ERROR
        private val IMPLEMENTATION = Implementation(
            SerializableObjectMissingReadResolveDetector::class.java,
            EnumSet.of(Scope.JAVA_FILE)
        )

        val MISSING_READ_RESOLVE_ISSUE: Issue = Issue.Companion.create(
            MISSING_READ_RESOLVE_ISSUE_ID,
            MISSING_READ_RESOLVE_ISSUE_DESCRIPTION,
            MISSING_READ_RESOLVE_ISSUE_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
        )

        private val JAVA_OBJECT: String = Object::class.java.canonicalName
    }

    override fun getApplicableUastTypes() =
        listOf<Class<out UElement>>(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? =
        object : UElementHandler() {

            override fun visitClass(node: UClass) {
                if (
                    node is KotlinUClass &&
                    node.ktClass is KtObjectDeclaration &&
                    isSerializable(node)
                ) {
                    if (node.doesNotHaveReadResolveMethod()) {
                        context.report(
                            MISSING_READ_RESOLVE_ISSUE,
                            node,
                            context.getLocation(node as PsiElement),
                            MISSING_READ_RESOLVE_ISSUE_EXPLANATION,
                            node.createMissingReadResolveLintFix()
                        )
                    } else {
                        node.methods
                            .filterIsInstance<KotlinUMethod>()
                            .filter { it.name == "readResolve" && !it.hasCorrectReadResolveSignature() }
                            .forEach { method: KotlinUMethod ->
                                node.methodSignatureCheck(method)
                            }
                    }
                }
            }

            fun UClass.methodSignatureCheck(method: KotlinUMethod) {
                name?.let { name ->
                    context.report(
                        MISSING_READ_RESOLVE_ISSUE,
                        context.getLocation(method),
                        INCORRECT_READ_RESOLVE_SIGNATURE_EXPLANATION,
                        method.createIncorrectMethodSignatureLintFix(name)
                    )
                }
            }

            fun isSerializable(node: UClass) =
                context.evaluator.implementsInterface(
                    node,
                    Serializable::class.java.canonicalName,
                    false
                )

            fun KotlinUClass.doesNotHaveReadResolveMethod(): Boolean =
                allMethods.none { it.name == "readResolve" }

            fun KotlinUMethod.hasCorrectReadResolveSignature(): Boolean =
                returnType?.canonicalText == JAVA_OBJECT && parameters.isEmpty()

            fun KotlinUClass.createMissingReadResolveLintFix(): LintFix {
                val objectSource = sourceElement.text
                val hasClassBody = objectSource.trimEnd().endsWith("}")
                return if (hasClassBody) {
                    LintFix.create()
                        .name("Add readResolve() method")
                        .replace()
                        .pattern("\\}\\s*$")
                        .with("\n@Suppress(\"UnusedPrivateMember\")\nprivate fun readResolve(): Any = $name\n}")
                        .reformat(true)
                        .autoFix()
                        .build()
                } else {
                    LintFix.create()
                        .name("Add readResolve() method")
                        .replace()
                        .text(LintFix.ReplaceString.INSERT_END)
                        .with(" {\n@Suppress(\"UnusedPrivateMember\")\nprivate fun readResolve(): Any = $name\n}")
                        .reformat(true)
                        .autoFix()
                        .build()
                }
            }

            fun KotlinUMethod.createIncorrectMethodSignatureLintFix(objectName: String): LintFix {
                return LintFix.create()
                    .name("Fix incorrect readResolve() method signature")
                    .replace()
                    .text(sourceElement.text)
                    .with("@Suppress(\"UnusedPrivateMember\")\nprivate fun readResolve(): Any = $objectName")
                    .reformat(true)
                    .autoFix()
                    .build()
            }
        }
}
