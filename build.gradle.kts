plugins {
    alias(libs.plugins.kotlin.jvm)
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
    implementation(libs.kotlin.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
