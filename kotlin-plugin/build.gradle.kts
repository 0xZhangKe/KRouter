plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    id("maven-publish")
}

group="com.zhangke.krouter"
version="0.1.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    kapt("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.9.20")
    compileOnly(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zhangke.krouter"
//            artifactId = "kotlin-plugin"
            version = "0.1.1"

            from(components["java"])
        }
    }
}
