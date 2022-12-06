plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("com.github.sqyyy.jnb:core:0.1.0-alpha")
    annotationProcessor("com.github.sqyyy.jnb:processor:0.1.0-alpha")
}