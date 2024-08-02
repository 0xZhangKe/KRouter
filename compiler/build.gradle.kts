plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = libs.versions.krouter.group.get()
version = libs.versions.krouter.version.get()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":core"))

    // 用于测试ksp处理器
    testImplementation("dev.zacsweers.kctfork:ksp:0.5.1")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation(libs.junit)

    implementation(libs.ksp.api)
    implementation("com.squareup:kotlinpoet:1.18.1")
    implementation("com.squareup:kotlinpoet-ksp:1.18.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "compiler"
            from(components["java"])
        }
    }
}
