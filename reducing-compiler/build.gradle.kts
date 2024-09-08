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
    implementation(project(":core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.17")
    implementation("com.squareup:kotlinpoet:1.16.0")
    implementation("com.squareup:kotlinpoet-ksp:1.16.0")
    implementation(project(":common"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "reducing-compiler"
            from(components["java"])
        }
    }
}
