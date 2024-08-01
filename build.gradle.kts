plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.devtools.ksp").version("1.9.22-1.0.17")
}

buildscript {
    dependencies {
        classpath("com.zhangke.krouter:plugin:0.2.1")
    }
}

apply(plugin = "krouter-plugin")

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}