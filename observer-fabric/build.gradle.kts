plugins {
    id("fabric-loom") version "1.11.7"
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
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    
    implementation(project(":observer-api"))
    include(project(":observer-api"))
    
    implementation(project(":observer-client-api"))
    include(project(":observer-client-api"))

    // UI Lib Fabric
    modImplementation("com.daqem.uilib:uilib-fabric:9.0.0")
    include("com.daqem.uilib:uilib-fabric:9.0.0")
}
