plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
        resources.srcDir("build/generated/ksp/main/resources")
    }
}

dependencies {
    implementation(project(":sample:sample-core"))
    implementation(project(":core"))
    ksp(project(":compiler"))
}
