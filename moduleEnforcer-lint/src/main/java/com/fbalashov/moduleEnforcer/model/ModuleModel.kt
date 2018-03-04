package com.fbalashov.moduleEnforcer.model

import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod

/**
 * @author Fuad.Balashov on 2/11/2018.
 */
data class ModuleModel(val qualifiedName: UClass, val requiredMethods: Set<UMethod>)