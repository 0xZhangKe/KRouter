pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
    includeBuild("gradle-plugin")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
rootProject.name = "KRoute"
include(":compiler")
include(":core")
val isInJitPack = System.getenv()["JITPACK"] == "true"
if (!isInJitPack) {
    include(":sample:profile")
    include(":sample:home")
    include(":sample:setting")
    include(":sample:sample-core")
    include(":sample:app")
}
//include("gradle-plugin")
include("kotlin-plugin")
