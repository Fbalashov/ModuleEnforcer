package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import junit.framework.TestCase
import com.fbalashov.moduleEnforcer.modules
import org.junit.Test

/**s
 * @author Fuad.Balashov on 2/19/2018.
 */
class ModuleDetectorJavaTest {

  @Test
  fun `WHEN there are no modules in the project`() {
    lint().files(nonModuleJava)
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(0, modules.size)
  }

  @Test
  fun `WHEN there is one module in the project`() {
    val result = lint().files(stubModuleJava, stubRequiredMethodJava, moduleOneJava)
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN there are multiple modules in the project`() {
    val result = lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        nonModuleJava,
        moduleOneJava,
        moduleTwoJava
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()

    //Assert
    result.expectClean()
    TestCase.assertEquals(2, modules.size)
    val class1Module = modules.find { it.qualifiedName.name == "ModuleClass1" }
    TestCase.assertEquals(1, class1Module!!.requiredMethods.size)
    val class2Module = modules.find { it.qualifiedName.name == "ModuleClass2" }
    TestCase.assertEquals(2, class2Module!!.requiredMethods.size)
  }
}