plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "+"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")  // VaultAPI
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC
}

dependencies {
    compileOnly(kotlin("stdlib")) // Kotlin
    compileOnly("io.github.monun:kommand-api:3.1.6")
    compileOnly("com.github.milkbowl:VaultAPI:+")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT") // Paper Latest
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
        archiveVersion.set("")
    }
    create<Copy>("dist") {
        from (shadowJar)
        into(".\\out\\")
    }
}