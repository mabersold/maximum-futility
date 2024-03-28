val ktorVersion = "2.3.7"
val kotlinVersion = "1.8.21"
val exposedVersion = "0.49.0"
val h2Version = "2.2.224"
val koinVersion = "3.5.1"
val mockkVersion = "1.13.9"

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.7"
    kotlin("plugin.serialization") version "1.8.21"
}

group = "mabersold"
version = "0.0.1"
application {
    mainClass.set("mabersold.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-thymeleaf-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}