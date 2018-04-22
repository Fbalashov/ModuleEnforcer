package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import com.fbalashov.moduleEnforcer.modules
import junit.framework.TestCase
import junit.framework.TestResult
import org.junit.Test

/**
 * @author Fuad.Balashov on 2/19/2018.
 */
class ModuleUsageDetectorKotlinTest {
  @Test
  fun `WHEN a module is used, AND the method is invoked`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClass1Kt()

              fun functionOne() {
                module.aFunction()
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND all required methods are called`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module1 = ModuleClass1Kt()
              private val module2 = ModuleClass2Kt()

              fun functionOne() {
                module1.aFunction()
                module2.aFunction()
              }

              fun functionTwo() {
                module2.bFunction()
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND the method is not invoked`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClass1Kt()

              fun functionOne() {
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
      |  private val module = ModuleClass1Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND required methods are not called`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module1 = ModuleClass1Kt()
              private val module2 = ModuleClass2Kt()

              fun functionOne() {
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
      |  private val module1 = ModuleClass1Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |2 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND the first's require methods are called AND the second's required methods are not called`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val  module1 = ModuleClass1Kt()
              private val module2 = ModuleClass2Kt()

              fun functionOne() {
                module1.aFunction()
              }
            }""").indented()
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND one of the second's required methods are not called`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleOneKt,
        moduleTwoKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module1 = ModuleClass1Kt()
              private val module2 = ModuleClass2Kt()

              fun functionOne() {
                module1.aFunction()
              }

              fun functionTwo() {
                module2.bFunction()
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:5: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
      |  private val module2 = ModuleClass2Kt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN a module WITH a required method with optional arguments is initialized, AND none of the methods are invoked`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOptionalArgsKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOptionalArgsKt()

              fun functionOne() {
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
      |  private val module = ModuleClassOptionalArgsKt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module WITH a required method with optional arguments is initialized, AND the method is invoked with all arguments`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOptionalArgsKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOptionalArgsKt()

              fun functionOne() {
                module.aFunction("def")
              }
            }""").indented()
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module WITH a required method with optional arguments is initialized, AND the method is invoked with no arguments`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOptionalArgsKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOptionalArgsKt()

              fun functionOne() {
                module.aFunction()
              }
            }""").indented()
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module WITH overloaded required methods is used, AND none of the methods are invoked`() {
    val result = TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOverloadedMethodsKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOverloadedMethodsKt()

              fun functionOne() {
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction, aFunction [RequiredMethodNotCalled]
      |  private val module = ModuleClassOverloadedMethodsKt()
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module WITH overloaded required methods is used, AND all of the methods are invoked`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOverloadedMethodsKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOverloadedMethodsKt()

              fun functionOne() {
                module.aFunction()
                module.aFunction("def")
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module WITH overloaded methods is used, AND one of which is required, AND the required method is invoked`() {
    TestLintTask.lint().files(
        stubModuleKt,
        stubRequiredMethodKt,
        moduleWithOverloadedMethodsOneRequiredKt,
        TestFiles.kt("""
            package moduleEnforcer.test

            class AClass {
              private val module = ModuleClassOverloadedMethodsKt()

              fun functionOne() {
                module.aFunction("abc")
              }
            }""").indented()
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

// Uncomment once I add support for overloaded methods!
// https://github.com/Fbalashov/ModuleEnforcer/issues/3
//  @Test
//  fun `WHEN a module WITH overloaded required methods is used, AND one of the methods are invoked`() {
//    val result = TestLintTask.lint().files(
//        stubModuleKt,
//        stubRequiredMethodKt,
//        moduleWithOverloadedMethodsKt,
//        TestFiles.kt("""
//            package moduleEnforcer.test
//
//            class AClass {
//              private val module = ModuleClassOverloadedMethodsKt()
//
//              fun functionOne() {
//                module.aFunction()
//              }
//            }""").indented()
//    )
//        .issues(ISSUE_MODULE_USAGE)
//        .run()
//    result.expect("""
//      |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
//      |  private val module = ModuleClassOverloadedMethodsKt()
//      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//      |1 errors, 0 warnings
//      |""".trimMargin()
//    )
//    TestCase.assertEquals(1, modules.size)
//  }
//
//  @Test
//  fun `WHEN a module WITH overloaded methods is used, AND one of which is required, AND the required method is not invoked`() {
//    val result = TestLintTask.lint().files(
//        stubModuleKt,
//        stubRequiredMethodKt,
//        moduleWithOverloadedMethodsOneRequiredKt,
//        TestFiles.kt("""
//              package moduleEnforcer.test
//
//              class AClass {
//                private val module = ModuleClassOverloadedMethodsKt()
//
//                fun functionOne() {
//                  module.aFunction()
//                }
//              }""").indented()
//    )
//        .issues(ISSUE_MODULE_USAGE)
//        .run()
//    result.expect("""
//        |src/moduleEnforcer/test/AClass.kt:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
//        |  private val module = ModuleClassOverloadedMethodsKt()
//        |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//        |1 errors, 0 warnings
//        |""".trimMargin()
//    )
//    TestCase.assertEquals(1, modules.size)
//  }
}