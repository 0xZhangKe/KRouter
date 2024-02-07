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

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

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
