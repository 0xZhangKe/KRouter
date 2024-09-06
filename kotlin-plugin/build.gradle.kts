plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    id("maven-publish")
}

group="com.zhangke.krouter"
version="0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
            }
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    kapt("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.9.20")
    compileOnly(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            from(components["kotlin"])
        }
    }
}
