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
include(":core")
include("collecting-compiler")
include("reducing-compiler")
val isInJitPack = System.getenv()["JITPACK"] == "true"
if (!isInJitPack) {
    include("common")
    include(":sample:profile")
    include(":sample:home")
    include(":sample:setting")
    include(":sample:sample-core")
    include(":sample:app")
}
