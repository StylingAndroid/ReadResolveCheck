const val kotlinVersion = "1.3.70-eap-274"
const val detektVersion = "1.5.0"

object Release {
    const val groupId = "com.stylingandroid"
    const val artifactId = "read-resolve-check"
    const val libraryVersion = "1.0.0"
}

object BuildPlugins {
    object Version {
        const val androidBuildToolsVersion = "4.0.0-alpha09"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Version.androidBuildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"

    const val detekt = "io.gitlab.arturbosch.detekt"
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val versions = "com.github.ben-manes.versions"
}

object AndroidSdk {
    const val min = 21
    const val compile = 28
    const val target = compile
}

object Libraries {
    private object Versions {
        const val appCompat = "1.2.0-alpha02"
        const val constraintLayout = "2.0.0-beta4"
        const val jupiter = "5.6.0"
        const val lint = "27.0.0-alpha09"
        const val detekt = detektVersion
        const val ktlint = "0.31.0"
        const val assertj = "3.15.0"
        const val mockito = "2.2.0"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val jupiter = "org.junit.jupiter:junit-jupiter:${Versions.jupiter}"
    const val lintApi = "com.android.tools.lint:lint-api:${Versions.lint}"
    const val lintTests = "com.android.tools.lint:lint-tests:${Versions.lint}"

    const val lint = ":lint"
}
