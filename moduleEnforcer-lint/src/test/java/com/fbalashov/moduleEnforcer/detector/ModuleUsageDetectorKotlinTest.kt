package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import com.fbalashov.moduleEnforcer.modules
import junit.framework.TestCase
import org.junit.Test

/**
 * @author Fuad.Balashov on 2/19/2018.
 */
class ModuleUsageDetectorKotlinTest {
  @Test
  fun `WHEN a module is used, AND the method is invoked, AND both classes are in kotlin`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val module = ModuleClass1Kt()
            |
            |  fun functionOne() {
            |    module.aFunction()
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND all required methods are called, AND all classes are in kotlin`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val module1 = ModuleClass1Kt()
            |  private val module2 = ModuleClass2Kt()
            |
            |  fun functionOne() {
            |    module1.aFunction()
            |    module2.aFunction()
            |  }
            |
            |  fun functionTwo() {
            |    module2.bFunction()
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND the method is not invoked, AND both classes are in kotlin`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val module = ModuleClass1Kt()
            |
            |  fun functionOne() {
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private val module = ModuleClass1Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND required methods are not called, AND all classes are in kotlin`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val module1 = ModuleClass1Kt()
            |  private val module2 = ModuleClass2Kt()
            |
            |  fun functionOne() {
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private val module1 = ModuleClass1Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |2 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND the first's require methods are called AND the second's required methods are not called, AND all classes are in kotlin`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val  module1 = ModuleClass1Kt()
            |  private val module2 = ModuleClass2Kt()
            |
            |  fun functionOne() {
            |    module1.aFunction()
            |  }
            |}""".trimMargin())
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND one of the second's required methods are not called, AND both classes are in kotlin`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  private val module1 = ModuleClass1Kt()
            |  private val module2 = ModuleClass2Kt()
            |
            |  fun functionOne() {
            |    module1.aFunction()
            |  }
            |
            |  fun functionTwo() {
            |    module2.bFunction()
            |  }
            |}""".trimMargin())
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }
}