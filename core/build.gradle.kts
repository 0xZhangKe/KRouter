plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}

group = libs.versions.krouter.group
version = libs.versions.krouter.version

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
}
