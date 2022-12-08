# Java Notebooks
Java Notebooks is an annotation-processor for Java.
It can generate metadata based on annotations at compiletime.
The goal is to manage several exercises in just one gradle or maven project.

## Integration (Gradle)
* Repository
* Dependency
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/sqyyy-jar/java-notebooks")
        credentials {
            username = System.getenv("GRADLE_GITHUB_USERNAME")
            password = System.getenv("GRADLE_GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.github.sqyyy.jnb:core:0.1.3-alpha")
    annotationProcessor("com.github.sqyyy.jnb:processor:0.1.3-alpha")
}
```
**NOTE: The credentials should have access to read GitHub Packages and may be stored elsewhere**
