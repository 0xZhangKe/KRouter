package com.zhangke.krouter

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.jvm.config.JavaSourceRoot
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.backend.Fir2IrExtensions
import org.jetbrains.kotlin.fir.backend.Fir2IrScriptConfiguratorExtension
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionPointName
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.resolve.FirSamConversionTransformerExtension
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import kotlin.reflect.KClass


@OptIn(ExperimentalCompilerApi::class)
@AutoService(FirExtensionRegistrar::class)
class KRouterMappingFirCompilerRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +KRouterMappingFirCompilerRegistrar::KRouterFirStatusTransformerExtension
    }

    class KRouterFirStatusTransformerExtension(session: FirSession): FirStatusTransformerExtension(session) {

        override fun transformStatus(status: FirDeclarationStatus, declaration: FirDeclaration): FirDeclarationStatus {
            return super.transformStatus(status, declaration)
        }

        override fun needTransformStatus(declaration: FirDeclaration): Boolean {
            return true
        }
    }
}

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class KRouterMappingCompilerRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = KRouterLogger(configuration)
        logger.i("Welcome to KRouter Kotlin-Plugin")
        logger.i("-----------------")
        logger.i("KRouterMappingCompilerRegistrar")
        logger.i("-----------------")

        val contentRoots = configuration[CLIConfigurationKeys.CONTENT_ROOTS] ?: emptyList()
        contentRoots.filterIsInstance<JavaSourceRoot>().map { it.file }
            .forEach {
                logger.i("---java file: $it")
            }

        AnalysisHandlerExtension.registerExtension(KRouterAnalysisHandlerExtension(logger))

        StorageComponentContainerContributor.registerExtension(KRouterStorageComponentContainer(logger))

        SyntheticResolveExtension.registerExtension(KRouterSyntheticResolveExtension(logger))

        IrGenerationExtension.registerExtension(KRouterMappingIrGenerationExtension(logger))
    }
}

class KRouterSyntheticResolveExtension(private val logger: KRouterLogger) : SyntheticResolveExtension {

    override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
        logger.i("---addSyntheticSupertypes->${thisDescriptor.name}")
        super.addSyntheticSupertypes(thisDescriptor, supertypes)
    }

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
        logger.i("---generateSyntheticMethods->${thisDescriptor.name}, $name, $bindingContext, $fromSupertypes, $result")
        super.generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)
    }

    override fun generateSyntheticClasses(
        thisDescriptor: ClassDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: ClassMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
    ) {
        logger.i("---generateSyntheticClasses->${thisDescriptor.name}, $name, $ctx, $declarationProvider, $result")
        super.generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
    }
}

class KRouterStorageComponentContainer(private val logger: KRouterLogger) : StorageComponentContainerContributor {

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        logger.i("---registerModuleComponents->${container}, $platform, $moduleDescriptor")
        super.registerModuleComponents(container, platform, moduleDescriptor)
    }
}

class KRouterAnalysisHandlerExtension(private val logger: KRouterLogger): AnalysisHandlerExtension{

    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        logger.i("---doAnalysis->${project.name}, ${module.name}, $projectContext")
        files.forEach {
            logger.i("---file: ${it.name}")
        }
        bindingTrace.bindingContext
        componentProvider.resolve(Class::class.java)
        return super.doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
    }

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        logger.i("---analysisCompleted->${project.name}, ${module.name}, $bindingTrace")
        files.forEach {
            logger.i("---file: ${it.name}")
        }
        return super.analysisCompleted(project, module, bindingTrace, files)
    }
}
