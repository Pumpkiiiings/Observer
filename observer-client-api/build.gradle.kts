plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5"
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    
    // Depends on observer-api to know about the models
    implementation(project(":observer-api"))
}
