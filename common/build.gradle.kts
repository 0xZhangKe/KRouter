plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = "com.zhangke.krouter"
version = "0.2.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("reflect"))
    api(project(":core"))
    implementation(libs.ksp.api)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}
