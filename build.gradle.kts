plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sqyyy-jar/java-notebooks")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

allprojects {
    group = "com.github.sqyyy.jnb"
    version = "0.1.0-alpha"

    repositories {
        mavenCentral()
    }
}