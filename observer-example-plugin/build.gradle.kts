plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation(project(":observer-api"))
    implementation(project(":observer-paper"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
