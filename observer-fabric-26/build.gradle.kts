plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

repositories {

    maven(url = "https://api.modrinth.com/maven") {
        name = "Modrinth"
    }
    maven {
        name = "DAQEM Studios Maven"
        url = uri("https://maven.daqem.com/releases")
    }
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev/")
    }
}
dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    implementation(project(":observer-api"))
    include(project(":observer-api"))
    
    implementation(project(":observer-client-api"))
    include(project(":observer-client-api"))

}
