plugins {
    id("java")
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin{
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("8"))
    }
}
