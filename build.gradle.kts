import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.publish)
    alias(libs.plugins.dokka)
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

extra["buildKRouterPom"] = { extension: MavenPublishBaseExtension, artifactName: String ->
    setupKRouterMavenPublication(extension, artifactName)
}

fun setupKRouterMavenPublication(extension: MavenPublishBaseExtension, artifactName: String) {
    with(extension) {

        coordinates("io.github.0xzhangke", artifactName, ProjectVersion.VERSION)
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        pom {
            name.set("KRouter")
            description.set("Lightweight Kotlin router.")
            url.set("https://github.com/0xZhangKe/KRouter")
            licenses {
                license {
                    name.set("Apache 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("zhangke")
                    name.set("Zhangke")
                    url.set("https://github.com/0xZhangKe/KRouter")
                }
            }
            scm {
                url.set("https://github.com/0xZhangKe/KRouter")
                connection.set("scm:git:git://github.com/0xZhangKe/KRouter.git")
                developerConnection.set("scm:git:ssh://git@github.com/0xZhangKe/KRouter.git")
            }
        }
    }
}

object ProjectVersion {
    // incompatible API changes
    private const val MAJOR = "0"

    // functionality in a backwards compatible manner
    private const val MONIR = "9"

    // backwards compatible bug fixes
    private const val PATH = "6"
    const val VERSION = "$MAJOR.$MONIR.$PATH"
}

tasks.dokkaHtmlMultiModule {
    moduleVersion.set(ProjectVersion.VERSION)
    outputDirectory.set(rootDir.resolve("docs/static/api"))
}
