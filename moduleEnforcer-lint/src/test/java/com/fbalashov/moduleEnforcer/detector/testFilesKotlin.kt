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

val nonModuleKt: LintDetectorTest.TestFile = TestFiles.kt("""
  |package moduleEnforcer.test
  |
  |class NonModuleClassKt {
  |  fun aFunction() {}
  |}""".trimMargin())

val moduleOneKt: LintDetectorTest.TestFile = TestFiles.kt("""
  |package moduleEnforcer.test
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod
  |
  |@Module
  |class ModuleClass1Kt {
  |  @RequiredMethod
  |  fun aFunction() {}
  |  fun bFunction() {}
  |}""".trimMargin())

val moduleTwoKt: LintDetectorTest.TestFile = TestFiles.kt("""
  |package moduleEnforcer.test
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod
  |
  |@Module
  |class ModuleClass2Kt {
  |  @RequiredMethod
  |  fun aFunction() {}
  |  @RequiredMethod
  |  fun bFunction(): Boolean {}
  |}""".trimMargin())

val moduleWithFieldsMethodsAnnotations: LintDetectorTest.TestFile = TestFiles.kt("""
  |package moduleEnforcer.test
  |
  |import com.fbalashov.moduleEnforcer.annotations.Module
  |import com.fbalashov.moduleEnforcer.annotations.RequiredMethod
  |import com.fbalashov.moduleEnforcer.annotations.AnotherAnnotation
  |
  |@Module
  |class ClassWithOtherFieldsAndMethods {
  |  var long: Long = 0
  |  var string: String? = null
  |
  |  @AnotherAnnotation
  |  @RequiredMethod
  |  fun aFunction(): Boolean {
  |    return false
  |  }
  |
  |  fun anotherFunction(): String {
  |    return ""
  |  }
  |}""".trimMargin())