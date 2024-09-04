plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.devtools.ksp").version("1.9.22-1.0.17")
//    id("com.zhangke.krouter.kotlin-plugin").version("0.1.1")
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    configurations.configureEach {
        resolutionStrategy{
            dependencySubstitution {
                substitute(module("com.zhangke.krouter:kotlin-plugin")).using(project(":kotlin-plugin"))
            }
        }
    }
}