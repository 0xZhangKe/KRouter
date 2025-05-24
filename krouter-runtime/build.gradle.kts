import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.zhangke.krouter"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }
}

kotlin {
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                api(project(":krouter-annotation"))
            }
        }
    }
}

mavenPublishing {
    @Suppress("UNCHECKED_CAST")
    val mavenPublicationSetup =
        rootProject.extra["buildKRouterPom"] as (MavenPublishBaseExtension, String) -> Unit
    mavenPublicationSetup(this, "krouter-runtime")
}
