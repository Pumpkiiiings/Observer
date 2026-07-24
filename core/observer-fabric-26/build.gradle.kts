plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.release.set(25)
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
    maven {
        name = "RedlanceMinecraft"
        url = uri("https://repo.redlance.org/public")
    }
}
dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    
    // PlayerAnimator
    implementation("com.zigythebird.playeranim:PlayerAnimationLibFabric:1.2.5+mc.26.1") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
    include("com.zigythebird.playeranim:PlayerAnimationLibFabric:1.2.5+mc.26.1")
    include("com.zigythebird.playeranim:PlayerAnimationLibCore:1.2.5+mc.26.1")

    implementation(project(":observer-api"))
    include(project(":observer-api"))
    
    implementation(project(":observer-client-api"))
    include(project(":observer-client-api"))

}
