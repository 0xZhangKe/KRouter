plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets.apply {
        targets.configureEach {
            compilations.configureEach {
                compileTaskProvider.configure {
                    compilerOptions {
                        // https://youtrack.jetbrains.com/issue/KT-61573
                        freeCompilerArgs.add("-Xexpect-actual-classes")
                    }
                }
            }
        }
    }

    sourceSets{
        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
    }
}

mavenPublishing {
    coordinates(libs.versions.krouter.group.get(), "core", libs.versions.krouter.version.get())

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)

    signAllPublications()
    pom {
        name.set("KRouter")
        description.set("Lightweight Kotlin router.")
        url.set("https://github.com/0xZhangKe/KRouter")
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
