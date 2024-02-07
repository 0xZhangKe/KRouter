plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

tasks.withType<ProcessResources>{
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
dependencies {
    implementation(project(":sample:sample-core"))
    implementation(project(":core"))
    ksp(project(":compiler"))
}
