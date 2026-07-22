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
    compileOnly("me.clip:placeholderapi:2.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    from(project(":observer-api").sourceSets.main.get().output)
}
