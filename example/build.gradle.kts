plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    manifest.attributes["Main-Class"] = "TestMain"
}

dependencies {
    implementation(project(":core"))
    annotationProcessor(project(":processor"))
}