plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradleup.shadow") version "8.3.5"
    `maven-publish`
}

group = "io.github.MH321Productions"
version = "0.1.0"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") //Spigot Repo
    maven("https://jitpack.io") //Vault Repo
    maven("https://repo.dmulloy2.net/repository/public/") //ProtocolLib
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.google.code.gson:gson:2.10")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}

tasks {
    processResources {
        expand(
            "version" to project.version
        )

        filteringCharset = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["kotlin"])
        }
    }
}