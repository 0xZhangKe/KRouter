import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("java")
    kotlin("jvm")
    alias(libs.plugins.dokka)
    id("com.vanniktech.maven.publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":krouter-runtime"))
    api(project(":krouter-compiler-framework"))
    implementation(libs.ksp.api)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}

mavenPublishing {
    @Suppress("UNCHECKED_CAST")
    val mavenPublicationSetup =
        rootProject.extra["buildKRouterPom"] as (MavenPublishBaseExtension, String) -> Unit
    mavenPublicationSetup(this, "krouter-reducing-compiler")
}
