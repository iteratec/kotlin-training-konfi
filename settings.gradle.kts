rootProject.name = "konfi"
include("backend", "solution-backend", "solution-frontend")

pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://plugins.gradle.org/m2/")
    }
}