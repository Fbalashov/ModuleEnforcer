package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE
import junit.framework.TestCase
import com.fbalashov.moduleEnforcer.modules
import org.junit.Test

/**
 * @author Fuad.Balashov on 2/19/2018.
 */
class ModuleUsageDetectorJavaTest {
  @Test
  fun `WHEN a module is used, AND the method is invoked`() {
    lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module = new ModuleClass1();
            |
            |  public void functionOne() {
            |    module.aFunction();
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND all required methods are called`() {
    lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        moduleTwoJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module1 = new ModuleClass1();
            |  private ModuleClass2 module2 = new ModuleClass2();
            |
            |  public void functionOne() {
            |    module1.aFunction();
            |    module2.aFunction();
            |  }
            |
            |  public void functionTwo() {
            |    module2.bFunction();
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND the method is not invoked`() {
    val result = lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module = new ModuleClass1();
            |
            |  public void functionOne() {
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.java:4: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private ModuleClass1 module = new ModuleClass1();
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND required methods are not called`() {
    val result = lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        moduleTwoJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module1 = new ModuleClass1();
            |  private ModuleClass2 module2 = new ModuleClass2();
            |
            |  public void functionOne() {
            |  }
            |}""".trimMargin())
    )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.java:4: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private ModuleClass1 module1 = new ModuleClass1();
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |src/moduleEnforcer/test/AClass.java:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private ModuleClass2 module2 = new ModuleClass2();
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |2 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND the first's require methods are called AND the second's required methods are not called`() {
    val result = lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        moduleTwoJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module1 = new ModuleClass1();
            |  private ModuleClass2 module2 = new ModuleClass2();
            |
            |  public void functionOne() {
            |    module1.aFunction();
            |  }
            |}""".trimMargin())
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.java:5: Error: Not all required methods in this module were invoked: aFunction, bFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private ModuleClass2 module2 = new ModuleClass2();
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }

  @Test
  fun `WHEN two modules are used, AND one of the second's required methods are not called`() {
    val result = lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        moduleTwoJava,
        java("""
            |package moduleEnforcer.test;
            |
            |public class AClass {
            |  private ModuleClass1 module1 = new ModuleClass1();
            |  private ModuleClass2 module2 = new ModuleClass2();
            |
            |  public void functionOne() {
            |    module1.aFunction();
            |  }
            |
            |  public void functionTwo() {
            |    module2.bFunction();
            |  }
            |}""".trimMargin())
        )
        .issues(ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.java:5: Error: Not all required methods in this module were invoked: aFunction [ModuleEnforcer_RequiredMethodNotCalled]
      |  private ModuleClass2 module2 = new ModuleClass2();
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(2, modules.size)
  }
}