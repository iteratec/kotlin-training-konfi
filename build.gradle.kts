plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false
    id("org.jetbrains.kotlin.js") version kotlinVersion apply false
}