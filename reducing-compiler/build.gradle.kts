plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = libs.versions.krouter.group
version = libs.versions.krouter.version

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":core"))
    api(project(":common"))
    implementation(libs.ksp.api)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "reducing-compiler"
            from(components["java"])
        }
    }
}
