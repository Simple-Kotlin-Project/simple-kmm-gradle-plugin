rootProject.name = "simple-kotlin-multiplatform-gradle-plugin"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./gradle/libs.version.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}