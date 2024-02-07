
plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin{
    jvmToolchain {
        implementation = JvmImplementation.VENDOR_SPECIFIC
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
        resources.srcDir("build/generated/ksp/main/resources")
    }
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

dependencies {
    implementation(project(":sample:home"))
    implementation(project(":sample:setting"))
    implementation(project(":sample:profile"))
    implementation(project(":sample:sample-core"))
    implementation(project(":core"))
    ksp(project(":compiler"))
}

tasks.withType<ProcessResources>{
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
