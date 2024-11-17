plugins {
    kotlin("jvm") version "2.0.20"
}

group = "io.github.MH321Productions"
version = "0.1.0"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") //Spigot Repo
    maven("https://jitpack.io") //Vault Repo

    maven { //ProtocolLib
        name = "dmulloy2-repo"
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    implementation("com.github.MilkBowl:VaultAPI:1.7")
    implementation("net.luckperms:api:5.4")
    implementation("com.comphenix.protocol:ProtocolLib:5.1.0")
}

tasks {
    processResources {
        expand("version" to project.version)

        filteringCharset = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(21)
}