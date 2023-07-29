plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = "com.zhangke.krouter"
version = "0.1.3"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("junit:junit:4.+")

    implementation(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "core"
            from(components["java"])
        }
    }
}
