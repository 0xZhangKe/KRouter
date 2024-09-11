import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    alias(libs.plugins.dokka)
    id("com.vanniktech.maven.publish")
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}

mavenPublishing {
    @Suppress("UNCHECKED_CAST")
    val mavenPublicationSetup =
        rootProject.extra["buildKRouterPom"] as (MavenPublishBaseExtension, String) -> Unit
    mavenPublicationSetup(this, "krouter-annotation")
}
