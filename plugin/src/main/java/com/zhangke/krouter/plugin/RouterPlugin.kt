package com.zhangke.krouter.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val threadId = Thread.currentThread().id
        println("[$threadId] apply: ${target.name}")

        goThroughSubsProject(target) { project, layer ->
            project.plugins.apply("com.google.devtools.ksp")

            project.dependencies.add(
                "ksp",
                "com.zhangke.krouter:compiler:0.2.1"
            )

            project.afterEvaluate {
                val kspExist = project.plugins.hasPlugin("com.google.devtools.ksp")
                println("[$threadId:$layer] apply subprojects: ${project.name}, kspExist: ${kspExist}")
            }
        }
    }
}

fun goThroughSubsProject(
    root: Project,
    layerIndex: Int = 0,
    callback: (project: Project, layer: Int) -> Unit
) {
    root.subprojects.forEach {
        callback(it, layerIndex)
        goThroughSubsProject(
            root = it,
            layerIndex = layerIndex + 1,
            callback = callback
        )
    }
}