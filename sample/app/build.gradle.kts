import com.bennyhuo.kotlin.ir.printer.gradle.OutputType

plugins {
    id("java")
    kotlin("jvm")
    id("com.bennyhuo.kotlin.ir.printer").version("1.9.20-1.1.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
        resources.srcDir("build/generated/ksp/main/resources")
    }
}

irPrinter {
    outputType = OutputType.RAW_IR
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

dependencies {
    implementation(project(":sample:home"))
    implementation(project(":sample:setting"))
    implementation(project(":sample:profile"))
    implementation(project(":sample:sample-core"))
    implementation(project(":core"))
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
