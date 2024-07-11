plugins {
    kotlin("jvm") version "2.0.0"
}

group = "net.tlalka.medium"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(11)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
