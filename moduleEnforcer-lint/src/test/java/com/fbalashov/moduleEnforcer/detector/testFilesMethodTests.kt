package com.fbalashov.moduleEnforcer.detector

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFiles

val moduleWithOptionalArgsKt: LintDetectorTest.TestFile = TestFiles.kt("""
  package moduleEnforcer.test

  import com.fbalashov.moduleEnforcer.annotations.Module
  import com.fbalashov.moduleEnforcer.annotations.RequiredMethod

  @Module
  class ModuleClassOptionalArgsKt {
    @RequiredMethod
    fun aFunction(string = "abc") {}
  }""").indented()

val moduleWithOverloadedMethodsKt: LintDetectorTest.TestFile = TestFiles.kt("""
  package moduleEnforcer.test

  import com.fbalashov.moduleEnforcer.annotations.Module
  import com.fbalashov.moduleEnforcer.annotations.RequiredMethod

  @Module
  class ModuleClassOverloadedMethodsKt {
    @RequiredMethod
    fun aFunction(string: String) {}
    @RequiredMethod
    fun aFunction() {}
  }""").indented()

val moduleWithOverloadedMethodsOneRequiredKt: LintDetectorTest.TestFile = TestFiles.kt("""
  package moduleEnforcer.test

  import com.fbalashov.moduleEnforcer.annotations.Module
  import com.fbalashov.moduleEnforcer.annotations.RequiredMethod

  @Module
  class ModuleClassOverloadedMethodsKt {
    @RequiredMethod
    fun aFunction(string: String) {}
    fun aFunction() {}
  }""").indented()

val moduleWithOverloadedMethodsJava: LintDetectorTest.TestFile = TestFiles.java("""
  package moduleEnforcer.test;

  import com.fbalashov.moduleEnforcer.annotations.Module;
  import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;

  @Module
  public class ModuleClassOverloadedMethodsJava {
    @RequiredMethod
    public void aFunction(String string) {}
    @RequiredMethod
    public void aFunction() {}
  }""").indented()


val moduleWithOverloadedMethodsOneRequiredJava: LintDetectorTest.TestFile = TestFiles.java("""
  package moduleEnforcer.test;

  import com.fbalashov.moduleEnforcer.annotations.Module;
  import com.fbalashov.moduleEnforcer.annotations.RequiredMethod;

  @Module
  public class ModuleClassOverloadedMethodsJava {
    @RequiredMethod
    public void aFunction(String string) {}
    public void aFunction() {}
  }""").indented()
