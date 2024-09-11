import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.config.LanguageFeature

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.dokka)
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {

        all {
            languageSettings { enableLanguageFeature(LanguageFeature.ExpectActualClasses.name) }
        }

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
