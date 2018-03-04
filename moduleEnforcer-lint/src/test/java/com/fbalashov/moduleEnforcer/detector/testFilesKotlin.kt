package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFiles

/**
 * @author Fuad.Balashov on 2/25/2018.
 * import inspiration from: https://github.com/vanniktech/lint-rules/blob/master/lint-rules-android-lint/src/test/java/com/vanniktech/lintrules/android/WrongTestMethodNameDetectorTest.kt
 */
val stubModuleKt: LintDetectorTest.TestFile = TestFiles.kt("""
  |package com.fbalashov.moduleEnforcer.annotations
  |
  |@Target(AnnotationTarget.CLASS)
  |@Retention(AnnotationRetention.SOURCE)
  |@MustBeDocumented
  |annotation class Module""".trimMargin())

val stubRequiredMethodKt: LintDetectorTest.TestFile = TestFiles.kt("""
  |package com.fbalashov.moduleEnforcer.annotations
  |
  |@Target(AnnotationTarget.FUNCTION)
  |@Retention(AnnotationRetention.SOURCE)
  |@MustBeDocumented
  |annotation class RequiredMethod""".trimMargin())

val nonModuleKt: LintDetectorTest.TestFile = TestFiles.java("""
  |package moduleEnforcer.test;
  |
  |class NonModuleClassKt {
  |  fun aFunction() {}
  |}""".trimMargin())

val moduleOneKt: LintDetectorTest.TestFile = TestFiles.java("""
  |package moduleEnforcer.test;
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module;
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;
  |
  |@Module
  |class ModuleClass1Kt {
  |  @RequiredMethod
  |  fun aFunction() {}
  |  fun bFunction() {}
  |}""".trimMargin())

val moduleTwoKt: LintDetectorTest.TestFile = TestFiles.java("""
  |package moduleEnforcer.test;
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module;
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;
  |
  |@Module
  |class ModuleClass2Kt {
  |  @RequiredMethod
  |  fun aFunction() {}
  |  @RequiredMethod
  |  fun bFunction() {}
  |}""".trimMargin())

// Kotlin File
//kt("" +
//    "package com.fbalashov.moduleEnforcer.detector\n" +
//    "\n" +
//    "class com.fbalashov.moduleEnforcer.detector.Module {\n" +
//    "  fun aMethod() {}\n" +
//    "}")