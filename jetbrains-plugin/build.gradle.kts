plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.mohesu"
version = "25.8.2707"

repositories {
    mavenCentral()
    gradlePluginPortal()
    
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2.4")
        bundledPlugin("com.intellij.java")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

// Configure IntelliJ Platform Plugin
intellijPlatform {
    pluginConfiguration {
    // Keep plugin version in sync with project version (updated by workflows)
    version = project.version.toString()
    }
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
        sinceBuild.set("242")
        untilBuild.set(provider { null })
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}