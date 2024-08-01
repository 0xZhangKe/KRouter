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
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.22-1.0.17")

    implementation(gradleApi())
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
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
