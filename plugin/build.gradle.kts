plugins {
    id("java")
    kotlin("jvm")
    id("java-gradle-plugin")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(gradleApi())
    implementation(libs.ksp.gradle)
    implementation(project(":core"))
}

gradlePlugin {
    plugins {
        create("krouter-plugin") {
            id = "krouter-plugin"
            implementationClass = "com.zhangke.krouter.plugin.RouterPlugin"
        }
    }
}

group = libs.versions.krouter.group.get()
version = libs.versions.krouter.version.get()

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "plugin"
            from(components["java"])
        }
    }
}
