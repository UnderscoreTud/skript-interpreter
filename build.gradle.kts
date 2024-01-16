plugins {
    id("java")
}

group = "me.tud"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.jetbrains:annotations:24.0.0")
}

tasks.test {
    useJUnitPlatform()
}