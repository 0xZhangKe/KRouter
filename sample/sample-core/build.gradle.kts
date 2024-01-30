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
        implementation = JvmImplementation.VENDOR_SPECIFIC
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
