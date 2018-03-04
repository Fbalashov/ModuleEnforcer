package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestFiles.java

/**
 * @author Fuad.Balashov on 2/25/2018.
 * import inspiration from: https://github.com/vanniktech/lint-rules/blob/master/lint-rules-android-lint/src/test/java/com/vanniktech/lintrules/android/WrongTestMethodNameDetectorTest.kt
 */
val stubModuleJava: LintDetectorTest.TestFile = TestFiles.java("""
  |package com.fbalashov.moduleEnforcer.annotations;
  |
  |public @interface Module { }""".trimMargin())

val stubRequiredMethodJava: LintDetectorTest.TestFile = TestFiles.java("""
  |package com.fbalashov.moduleEnforcer.annotations;
  |public @interface RequiredMethod { }""".trimMargin())

val nonModuleJava: LintDetectorTest.TestFile = java("""
  |package moduleEnforcer.test;
  |
  |public class NonModuleClass {
  |  public void aFunction() {}
  |}""".trimMargin())

val moduleOneJava: LintDetectorTest.TestFile = java("""
  |package moduleEnforcer.test;
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module;
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;
  |
  |@Module
  |public class ModuleClass1 {
  |  @RequiredMethod
  |  public void aFunction() {}
  |  public void bFunction() {}
  |}""".trimMargin())

val moduleTwoJava: LintDetectorTest.TestFile = java("""
  |package moduleEnforcer.test;
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module;
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;
  |
  |@Module
  |public class ModuleClass2 {
  |  @RequiredMethod
  |  public void aFunction() {}
  |  @RequiredMethod
  |  public void bFunction() {}
  |}""".trimMargin())