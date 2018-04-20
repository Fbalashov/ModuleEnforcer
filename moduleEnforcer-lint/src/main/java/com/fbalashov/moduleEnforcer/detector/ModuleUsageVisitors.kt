package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.fbalashov.moduleEnforcer.annotations.Module
import com.fbalashov.moduleEnforcer.annotations.RequiredMethod
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import com.fbalashov.moduleEnforcer.model.ModuleModel
import com.fbalashov.moduleEnforcer.modules
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UField
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.util.isMethodCall
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Detects Modules that a user has defined in their project as well as their required methods.
 * Uses the @ModuleModel and @RequiredMethod annotations.
 */
class ModuleFinder: UElementHandler() {
  override fun visitClass(node: UClass) {
    val hasModuleAnnotation = node.findAnnotation(Module::class.qualifiedName!!) != null
    if (hasModuleAnnotation) {
      val requiredMethods = node.methods
          .filter { method -> method.findAnnotation(RequiredMethod::class.qualifiedName!!) != null }
      modules.add(ModuleModel(node, requiredMethods.toSet()))
    }
  }
}

/**
 * Finds classes that have a field that is an @Module. If it finds any, it will use the InvocationListener to
 * track all calls to the @Modules. Once it finishes visiting all calls, it will validate that all
 * @requiredMethods were called.
 *
 * Pattern taken from: https://github.com/JetBrains/kotlin/blob/951868f5906a74c3b0303f1c76c52a0741eb2b75/plugins/
 * lint/lint-checks/src/com/android/tools/klint/checks/ViewHolderDetector.java
 */
class ClassVisitor(private val context: JavaContext): UElementHandler() {

  override fun visitClass(node: UClass) {
    // construct a list of all fields that are modules
    val modulesUsed = node.fields
        .filter { field -> field.type is PsiClassReferenceType }
        .map { field ->
          val fieldType = (field.type as PsiClassReferenceType).className ?: return@map null
          val module = modules.find{fieldType == it.qualifiedName.name} ?: return@map null
          return@map field to module
        }
        .filterNotNull()
        .toMap()

    if (modulesUsed.isEmpty()) {
      // most classes won't be using modules, just skip them
      return
    }

    // for each module, check if the RequiredMethods are called
    val visitor = InvocationVisitor(context, modulesUsed)
    node.accept(visitor)
    visitor.validateModuleCalls()
  }
}

/**
 * Takes in a set of modules and looks over a class for calls to those modules.
 * Visits all method calls in a class and tracks them if they are to one of the given modules.
 * When validateModuleCalls is invoked, the InvocationVisitor will validate that all @RequiredMethods were
 * called.
 *
 * Based on: https://github.com/vanniktech/lint-rules/blob/883c1461164f350cacccb3aa4014730886a50cce/
 * lint-rules-rxjava2-lint/src/main/java/com/vanniktech/lintrules/rxjava2/RxJava2MissingCompositeDisposableClearDetector.java
 */
class InvocationVisitor(private val context: JavaContext, private val modules: Map<UField, ModuleModel>): AbstractUastVisitor() {
  private val modulesCalls = mutableMapOf<ModuleModel, MutableSet<String?>>()
  init {
    for (module in modules.values) {
      // todo: see if there is a more kotlin way to init this without having moduleCalls be mutable
      modulesCalls[module] = mutableSetOf()
    }
  }

  override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression): Boolean {
    if (!node.selector.isMethodCall()) {
      // I would like to use visitCallExpression but it doesn't catch all java method invocations.
      // So instead I use this visit method.
      return false
    }
    val module = modules.values.find{ (qualifiedName) -> qualifiedName.qualifiedName == node.receiver.getExpressionType()?.canonicalText} ?: return false
    modulesCalls[module]?.add(node.resolvedName)
    return true
  }

  fun validateModuleCalls() {
    // assert that all the expected methods are called
    // report if there are unpainted methods on the node with the module declaration
    modules.forEach { (fieldNode, module) ->
      val moduleCalls = modulesCalls[module] ?: return@forEach
      val unCalledMethods = module.requiredMethods.filter {method -> moduleCalls.find { it == method.name } == null}
      if (!unCalledMethods.isEmpty()) {
        val missingCalls = unCalledMethods.joinToString { it.name }
        context.report(ISSUE_MODULE_USAGE, fieldNode, context.getLocation(fieldNode), "Not all required methods in this module were invoked: $missingCalls")
      }
    }
  }
}