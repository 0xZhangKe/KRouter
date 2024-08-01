package com.zhangke.krouter.plugin

import com.google.devtools.ksp.gradle.KspExtension
import com.zhangke.krouter.plugin.RouterPlugin.Companion.COMPILER_NOTATION
import com.zhangke.krouter.plugin.RouterPlugin.Companion.KSP_ID
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

class RouterPlugin : Plugin<Project> {
    companion object {
        const val KSP_ID = "com.google.devtools.ksp"
        const val COMPILER_NOTATION = "com.zhangke.krouter:compiler:0.2.1"
    }

    override fun apply(target: Project) {
        println("[apply]: ${target.name}")

        target.afterEvaluate {
            val targetInjectProjectName = target.extensions.extraProperties
                .runCatching { get("targetInjectProjectName") }
                .getOrNull()

            // 若不存在则直接返回
            if (targetInjectProjectName == null) {
                println("Target inject project name not found")
                return@afterEvaluate
            }

            val isInjectProject: (Project) -> Boolean = {
                it.name == targetInjectProjectName
            }

            // 获取需要注入的project
            val targetInjectProject = target.takeIf(isInjectProject)
                ?: target.subprojects.firstOrNull(isInjectProject)

            // 若不存在则直接返回
            if (targetInjectProject == null) {
                println("Target inject project not found")
                return@afterEvaluate
            }

            targetInjectProject.let {
                it.plugins.apply(KSP_ID)
                it.dependencies.add("ksp", COMPILER_NOTATION)

                it.beforeEvaluate { pro ->
                    pro.extensions
                        .getByType(KspExtension::class.java)
                        .arg("kRouterType", "inject")
                }
                it.afterEvaluate { project ->
                    goThroughProjectDependency(
                        root = project,
                        doInject = { project != it }
                    )
                }
            }
        }
    }
}

fun goThroughProjectDependency(
    root: Project,
    doInject: (project: Project) -> Boolean = { true }
) {
    if (doInject(root)) {
        root.plugins.apply(KSP_ID)
        root.dependencies.add("ksp", COMPILER_NOTATION)
        root.beforeEvaluate { pro ->
            pro.extensions
                .getByType(KspExtension::class.java)
                .arg("kRouterType", "collect")
        }
    }

    val dependencyProjects = root.configurations
        .map { it.dependencies.filterIsInstance<ProjectDependency>() }
        .flatten()
        .map { it.dependencyProject }
        .takeIf { it.isNotEmpty() }
        ?: return

    println("Project dependencies [${dependencyProjects.size}]")
    dependencyProjects.forEach { println("Project dependencies: $it") }

    dependencyProjects.forEach {
        goThroughProjectDependency(it)
    }
}