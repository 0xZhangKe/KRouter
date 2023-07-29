plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("junit:junit:4.+")

    implementation(kotlin("reflect"))
}
