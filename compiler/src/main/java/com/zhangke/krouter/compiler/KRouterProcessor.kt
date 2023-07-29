package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.zhangke.krouter.Destination

class KRouterProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KRouterProcessor(environment)
    }
}

class KRouterProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .toList()
            .forEach { it.accept(KRouterVisitor(environment), Unit) }
        return emptyList()
    }
}

class KRouterVisitor(
    private val environment: SymbolProcessorEnvironment
) : KSVisitorVoid() {

    companion object {
        private val badTypeName = Unit::class.qualifiedName
        private val badSuperTypeName = Any::class.qualifiedName
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val superTypeName = findSuperType(classDeclaration)
        writeService(superTypeName, classDeclaration)
    }

    private fun findSuperType(classDeclaration: KSClassDeclaration): String {
        val className = classDeclaration.qualifiedName?.asString().orEmpty()
        val routerAnnotation = classDeclaration.requireAnnotation<Destination>()
        val typeFromAnnotation = routerAnnotation.findArgumentTypeByName("type")
            ?.takeIf { it != badTypeName }
        if (typeFromAnnotation != null) {
            val superTypesIterator = classDeclaration.superTypes.iterator()
            var find = false
            while (superTypesIterator.hasNext()) {
                val type = superTypesIterator.next()
                if (type.typeQualifiedName == typeFromAnnotation) {
                    find = true
                    break
                }
            }
            if (!find) {
                val errorMessage = "Can't find $typeFromAnnotation from $className super type!"
                errorLog(errorMessage)
                throw IllegalArgumentException(errorMessage)
            }
            return typeFromAnnotation
        }
        if (classDeclaration.superTypes.isSingleElement()) {
            val superTypeName = classDeclaration.superTypes
                .iterator()
                .next()
                .typeQualifiedName
                ?.takeIf { it != badSuperTypeName }
            if (!superTypeName.isNullOrEmpty()) {
                return superTypeName
            }
        }
        val errorMessage = "Can't find Route type from $className"
        errorLog(errorMessage)
        throw IllegalArgumentException(errorMessage)
    }

    private fun writeService(
        superTypeName: String,
        serviceClassDeclaration: KSClassDeclaration,
    ) {
        val resourceFileName = ServicesFiles.getPath(superTypeName)
        val serviceClassFullName = serviceClassDeclaration.qualifiedName!!.asString()
        val existsFile = environment.codeGenerator
            .generatedFile
            .firstOrNull { generatedFile ->
                generatedFile.canonicalPath.endsWith(resourceFileName)
            }
        if (existsFile != null) {
            val services = existsFile.inputStream().use { ServicesFiles.readServiceFile(it) }
            services.add(serviceClassFullName)
            existsFile.outputStream().use { ServicesFiles.writeServiceFile(services, it) }
        } else {
            environment.codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, serviceClassDeclaration.containingFile!!),
                packageName = "",
                fileName = resourceFileName,
                extensionName = "",
            ).use {
                ServicesFiles.writeServiceFile(setOf(serviceClassFullName), it)
            }
        }
    }

    private fun errorLog(msg: String) {
        environment.logger.error(msg)
    }
}
