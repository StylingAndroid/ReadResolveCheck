pipeline {

  agent any

  environment {
        BINTRAY_USER = credentials('bintrayUser')
        BINTRAY_API_KEY = credentials('bintrayApiKey')
  }

  stages {
      stage('Checkout') {
        steps {
            checkout scm
        }
      }

      stage('Build') {
        steps {
            sh "./gradlew clean assemble"
        }
      }

      stage('Check') {
        steps {
            sh "./gradlew detekt check ktlintCheck jacocoTestReport"
        }
      }

      stage('Report') {
        steps {
            recordIssues healthy: 2, unhealthy: 3, tool: androidLintParser(pattern: '**/reports/**/lint-results.xml', reportEncoding: 'UTF-8')
            recordIssues tool: detekt(pattern: '**/reports/**/detekt.xml', reportEncoding: 'UTF-8')
            recordIssues tool: ktLint(pattern: '**/reports/**/ktlint*.xml', reportEncoding: 'UTF-8')
            junit allowEmptyResults: true, testResults: '**/build/test-results/**/*.xml'
            publishCoverage adapters: [jacocoAdapter('**/build/reports/jacoco/**/*.xml')], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')
        }
      }
  }
}
