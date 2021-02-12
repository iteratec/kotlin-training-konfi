plugins {
    id("org.jetbrains.kotlin.js")
    kotlin("plugin.serialization") version "1.4.21"
}

group = "de.iteratec.konfi"
version = "unspecified"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("org.jetbrains:kotlin-react:17.0.1-pre.144-kotlin-1.4.21")
    implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.144-kotlin-1.4.21")
    implementation(npm("react", "17.0.1"))
    implementation(npm("react-dom", "17.0.1"))

    implementation(npm("bootstrap", "4.6.0"))

    testImplementation(kotlin("test-js"))
}

kotlin {
    js {
        moduleName = "konfi"

        browser {
            commonWebpackConfig {
                outputFileName = "konfi.js"
                cssSupport.enabled = true
                devServer = devServer?.copy(port = 9090)
            }
        }
    }
}