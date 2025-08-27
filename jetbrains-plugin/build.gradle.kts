plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.mohesu"
version = "25.8.2702"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2024.2.4")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf())
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set(provider { null })
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}