plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
}

// 将plugin和compiler发布到MavenLocal后，将下列代码取消注释，即可启用plugin
// 同时需要避免在jitpack打包的环境中应用plugin

//buildscript {
//    if (System.getenv()["JITPACK"] != "true") {
//        dependencies { classpath("com.zhangke.krouter:plugin:0.2.1") }
//    }
//}
//
//if (System.getenv()["JITPACK"] != "true") {
//    ext { set("targetInjectProjectName", "app") }
//    apply(plugin = "krouter-plugin")
//}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}