plugins {
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "+"
}

group = "com.koisv"
version = "R1.3-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")  // VaultAPI
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC
}

dependencies {
    compileOnly(kotlin("stdlib")) // Kotlin
    compileOnly("com.github.milkbowl:VaultAPI:+")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT") // Paper Latest
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    shadowJar {
        archiveClassifier.set("dist")
    }
    create<Copy>("dist") {
        from (shadowJar)
        into(".\\out\\")
    }
}