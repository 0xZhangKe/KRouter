package com.zhangke.krouter

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

class KRouterMappingIrGenerationExtension(private val logger: KRouterLogger): IrGenerationExtension {

    private val mappingTransformer = KRouterMappingTransformer(logger)

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.getPackageFragment()
        val platform = pluginContext.platform
        logger.i("---------generate start----------")
        logger.i("platform: $platform")
        for (irFile in moduleFragment.files) {
            irFile.transform(mappingTransformer, null)
        }
        when {
//            platform.isJvm() -> {
//                val atomicSymbols = JvmAtomicSymbols(pluginContext, moduleFragment)
//                AtomicfuJvmIrTransformer(pluginContext, atomicSymbols).transform(moduleFragment)
//            }
//            platform.isNative() -> {
//                val atomicSymbols = NativeAtomicSymbols(pluginContext, moduleFragment)
//                AtomicfuNativeIrTransformer(pluginContext, atomicSymbols).transform(moduleFragment)
//            }
//            platform.isJs() -> {
//                for (file in moduleFragment.files) {
//                    AtomicfuClassLowering(pluginContext).runOnFileInOrder(file)
//                }
//            }
        }
        logger.i("---------generate end----------")
    }
}

class KRouterMappingTransformer(private val logger: KRouterLogger): IrElementTransformer<IrFunction?>{

    override fun visitClass(declaration: IrClass, data: IrFunction?): IrStatement {
        logger.i("visitClass: name:${declaration.name}, annotations:${declaration.annotations}, superClass:${declaration.superClass}, superTypes:${declaration.superTypes}, data:$data")
        return super.visitClass(declaration, data)
    }

    override fun visitBlock(expression: IrBlock, data: IrFunction?): IrExpression {
        logger.i("visitBlock: $expression, $data")
        return super.visitBlock(expression, data)
    }

    override fun visitBody(body: IrBody, data: IrFunction?): IrBody {
        logger.i("visitBody: $body, $data")
        return super.visitBody(body, data)
    }

    override fun visitFunction(declaration: IrFunction, data: IrFunction?): IrStatement {
        logger.i("visitFunction: $declaration, $data")
        return super.visitFunction(declaration, data)
    }
}
