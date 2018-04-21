package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import junit.framework.TestCase
import com.fbalashov.moduleEnforcer.modules
import org.junit.Test

/**
 * @author Fuad.Balashov on 2/19/2018.
 */
class ModuleDetectorKotlinTest {

  @Test
  fun `WHEN there are no modules in the project`() {
    lint().files(nonModuleKt)
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(0, modules.size)
  }

  @Test
  fun `WHEN there is one module in the project AND it's in kotlin`() {
    val result = lint().files(stubModuleKt, stubRequiredMethodKt, moduleOneKt)
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN there is one module in the project AND it has other methods, fields and annotations AND it's in kotlin`() {
    val result = lint().files(stubModuleKt, stubRequiredMethodKt, moduleWithFieldsMethodsAnnotationsKt)
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expectClean()
    TestCase.assertEquals(1, modules.size)
    val class1Module = modules.find { it.qualifiedName.name == "ClassWithOtherFieldsAndMethods" }
    TestCase.assertEquals(1, class1Module!!.requiredMethods.size)
  }

  @Test
  fun `WHEN there are multiple modules in the project AND they're in kotlin`() {
    val result = lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        nonModuleKt,
        moduleOneKt,
        moduleTwoKt
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()

    //Assert
    result.expectClean()
    TestCase.assertEquals(2, modules.size)
    val class1Module = modules.find { it.qualifiedName.name == "ModuleClass1Kt" }
    TestCase.assertEquals(1, class1Module!!.requiredMethods.size)
    val class2Module = modules.find { it.qualifiedName.name == "ModuleClass2Kt" }
    TestCase.assertEquals(2, class2Module!!.requiredMethods.size)
  }
}