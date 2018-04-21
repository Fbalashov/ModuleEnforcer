package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import com.fbalashov.moduleEnforcer.modules
import junit.framework.TestCase
import org.junit.Test

/**
 * @author Fuad.Balashov on 4/21/2018.
 */
class ClassParsingKotlinTest {

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the module has other fields, methods and annotations`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithFieldsMethodsAnnotationsKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  fun functionOne(): String {
            |    return module1.aFunction()
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the the required method takes arguments`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithArgsKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  fun functionOne(): String {
            |    return module1.aFunction("abd")
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the module user has other fields, methods and annotations`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |import com.fbalashov.moduleEnforcer.annotations.AnotherAnnotation
            |
            |class AClass {
            |  private val module1 = ModuleClass1Kt()
            |  private val another = "asdf"
            |  public var aValue: Boolean?
            |
            |  fun functionOne(): String {
            |    return module1.aFunction()
            |  }
            |
            |  @AnotherAnnotation
            |  fun functionTwo(): Boolean {
            |    return false;
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is injected, AND the method is not invoked`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            |package moduleEnforcer.test
            |
            |class AClass {
            |  @Inject lateinit var module: ModuleClass1Kt
            |
            |  fun functionOne() {
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  @Inject lateinit var module: ModuleClass1Kt
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }
}