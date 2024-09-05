plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.9.22"
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

group = "com.zhangke.krouter"
version = "0.0.1"

gradlePlugin {
    plugins {
        create("krouter-plugin") {
            id = "krouter-plugin" // `apply plugin: "krouter.plugin"
            implementationClass = "com.zhangke.krouter.KRouterPlugin" // entry-point class
        }
    }
}

dependencies {
    compileOnly(kotlin("gradle-plugin-api"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
