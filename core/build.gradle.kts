plugins {
    java
}

allprojects {
    apply(plugin = "java")
    
    group = "com.observer"
    version = "1.0.1"

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.withType<JavaCompile> {
        options.release.set(21)
    }
}
