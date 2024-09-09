import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.publish)
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}


allprojects {
    group = "com.zhangke.krouter"
    version = "0.9.0"

    plugins.withId("om.vanniktech.maven.publish.base") {
        mavenPublishing {
            publishToMavenCentral(SonatypeHost.DEFAULT)
            signAllPublications()

            pom {
                name.set("KRouter")
                description.set("Lightweight Kotlin router.")
                url.set("https://github.com/0xZhangKe/KRouter")
                developers {
                    developer {
                        id.set("zhangke")
                        name.set("Zhangke")
                        url.set("https://github.com/0xZhangKe/KRouter")
                    }
                }
                scm {
                    url.set("https://github.com/0xZhangKe/KRouter")
                    connection.set("scm:git:git://github.com/0xZhangKe/KRouter.git")
                    developerConnection.set("scm:git:ssh://git@github.com/0xZhangKe/KRouter.git")
                }
            }
        }
    }
}