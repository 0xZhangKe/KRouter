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

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

dependencies {
    testImplementation(libs.junit)

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
