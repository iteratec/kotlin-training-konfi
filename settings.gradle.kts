rootProject.name = "konfi"
include("backend")

pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://plugins.gradle.org/m2/")
    }
}