pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add this line for MPAndroidChart
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "exam_06"
include(":app")