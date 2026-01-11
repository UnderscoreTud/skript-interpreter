import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
}

group = "me.tud.skriptinterpreter"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    api(libs.jetbrains.annotations)
    implementation(libs.slf4j.api)

    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
