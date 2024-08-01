plugins {
    id("java")
    kotlin("jvm")
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    val kotlinVersion = "1.9.22"

    // https://mvnrepository.com/artifact/dev.zacsweers.kctfork/core
    testImplementation("dev.zacsweers.kctfork:core:0.5.1")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    testImplementation("junit:junit:4.13.2")

    implementation(gradleApi())
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation(project(":core"))

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

gradlePlugin {
    plugins {
        create("krouter-plugin") {
            id = "krouter-plugin"
            implementationClass = "com.zhangke.krouter.plugin.RouterPlugin"
        }
    }
}

group = "com.zhangke.krouter"
version = "0.2.1"

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "plugin"
            from(components["java"])
        }
    }
}
