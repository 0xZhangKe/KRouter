pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "KRouter"
include(":krouter-runtime")
include(":krouter-reducing-compiler")
include(":krouter-collecting-compiler")
include(":krouter-annotation")
include(":krouter-compiler-framework")
include(":sample:profile")
include(":sample:home")
include(":sample:setting")
include(":sample:sample-core")
include(":sample:app")