pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "KRoute"
include(":compiler")
include(":core")
include(":sample:profile")
include(":sample:home")
include(":sample:setting")
include(":sample:sample-core")
include(":sample:app")
