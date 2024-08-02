plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    if (System.getenv()["JITPACK"] != "true") {
        dependencies { classpath("com.zhangke.krouter:plugin:0.2.1") }
    }
}

// 避免在jitpack打包的环境中应用plugin
if (System.getenv()["JITPACK"] != "true") {
    ext { set("targetInjectProjectName", "app") }
    apply(plugin = "krouter-plugin")
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}