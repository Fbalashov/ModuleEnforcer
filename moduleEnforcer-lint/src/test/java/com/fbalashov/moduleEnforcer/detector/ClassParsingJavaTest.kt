package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.fbalashov.moduleEnforcer.Issues
import com.fbalashov.moduleEnforcer.modules
import junit.framework.TestCase
import org.junit.Test

/**
 * @author Fuad.Balashov on 4/21/2018.
 */
class ClassParsingJavaTest {

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the module has other fields, methods and annotations`() {
    TestLintTask.lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleWithFieldsMethodsAnnotationsJava,
        TestFiles.java("""
            package moduleEnforcer.test;

            public class AClass {
              private ClassWithOtherFieldsAndMethods module1 = new ClassWithOtherFieldsAndMethods();

              public void functionOne() {
                module1.aFunction();
              }
            }
            """).indented()
        )
        .issues(Issues.ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the module user has other fields, methods and annotations`() {
    TestLintTask.lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        TestFiles.java("""
            package moduleEnforcer.test;

            import com.fbalashov.moduleEnforcer.annotations.AnotherAnnotation;

            public class AClass {
              private moduleOneJava module1 = new moduleOneJava();
              private String anotherField = "asdasd";
              public boolean defaultvalue;

              public void functionOne() {
                module1.aFunction();
              }

              @AnotherAnnotation
              public boolean functionTwo() {
                return false;
              }
            }""").indented()
    )
        .issues(Issues.ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is used, AND all methods are called, AND the required method takes arguments`() {
    TestLintTask.lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleWithArgsJava,
        TestFiles.java("""
            package moduleEnforcer.test;

            public class AClass {
              private ModuleClassArgs module1 = new ModuleClassArgs();

              public void functionOne() {
                module1.aFunction("abc");
              }
            }""").indented()
    )
        .issues(Issues.ISSUE_MODULE_USAGE)
        .run()
        .expectClean()
    TestCase.assertEquals(1, modules.size)
  }

  @Test
  fun `WHEN a module is injected, AND the method is not invoked`() {
    val result = TestLintTask.lint().files(
        stubModuleJava,
        stubRequiredMethodJava,
        moduleOneJava,
        TestFiles.java("""
            package moduleEnforcer.test;

            public class AClass {
              @Inject ModuleClass1 module;

              public void functionOne() {
              }
            }""").indented()
    )
        .issues(Issues.ISSUE_MODULE_USAGE)
        .run()
    result.expect("""
      |src/moduleEnforcer/test/AClass.java:4: Error: Not all required methods in this module were invoked: aFunction [RequiredMethodNotCalled]
      |  @Inject ModuleClass1 module;
      |  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      |1 errors, 0 warnings
      |""".trimMargin()
    )
    TestCase.assertEquals(1, modules.size)
  }
}