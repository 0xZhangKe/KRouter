plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
        resources.srcDir("build/generated/ksp/main/resources")
    }
}

tasks.withType<ProcessResources>{
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

dependencies {
    implementation(project(":sample:sample-core"))
    implementation(project(":core"))
    ksp(project(":collecting-compiler"))
}
