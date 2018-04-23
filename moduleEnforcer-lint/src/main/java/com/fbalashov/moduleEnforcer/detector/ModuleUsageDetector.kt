package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.fbalashov.moduleEnforcer.modules
import org.jetbrains.uast.*

/**
 * A 2 pass Detector that verifies that any classes that use a @Module annotated class, call all of its
 * @RequiredMethod annotated methods.
 *
 * Pass 1: Detects Modules that a user has defined in their project as well as their required methods.
 * Uses the @ModuleModel and @RequiredMethod annotations.
 *
 * Pass 2: Detects fields that are @Modules and validates that all @RequiredMethods are called.
 *
 * @author Fuad.Balashov on 2/11/2018.
 *
 * Based on: https://github.com/JetBrains/kotlin/blob/951868f5906a74c3b0303f1c76c52a0741eb2b75/plugins/
 * lint/lint-checks/src/com/android/tools/klint/checks/ViewHolderDetector.java
 */
class ModuleUsageDetector: Detector(), Detector.UastScanner {
  init {
    modules.clear()
  }

  override fun getApplicableUastTypes() = mutableListOf(UClass::class.java)

  override fun createUastHandler(context: JavaContext): UElementHandler {
    if (context.driver.phase < 2) {
      return ModuleFinder()
    }
    return ClassVisitor(context)
  }

  override fun afterCheckProject(context: Context) {
    super.afterCheckProject(context)
    val driver = context.driver
    if (driver.phase == 1) {
      driver.requestRepeat(this, Scope.JAVA_FILE_SCOPE)
    }
  }
}