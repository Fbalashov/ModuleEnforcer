package com.fbalashov.moduleEnforcer;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.fbalashov.moduleEnforcer.detector.ModuleUsageDetector;

/**
 * @author Fuad.Balashov on 3/4/2018.
 * I had issues accessing the companion object when running lint on kotlin code specifically
 * By putting this in a java class I can get around it.
 *
 * ERROR:
 * > Task :app:lint
    Could not load custom lint check jar file C:\Users\admin\.gradle\caches\transforms-1\files-1.1\moduleEnforcer-android-release.aar\8650ded916a684230d99fdad787faa3f\jars\lint.jar
    java.lang.NoSuchFieldError: Companion
    at com.fbalashov.moduleEnforcer.detector.ModuleUsageVisitorsKt.<clinit>(ModuleUsageVisitors.kt:16)
    at com.fbalashov.moduleEnforcer.Registry.getIssues(Registry.kt at com.android.tools.lint.client.api.JarFileIssueRegistry.<init>(JarFileIssueRegistry.kt:53)
    at com.android.tools.lint.client.api.JarFileIssueRegistry.<init>(JarFileIssueRegistry.kt:45)
    at com.android.tools.lint.client.api.JarFileIssueRegistry$Factory.get(JarFileIssueRegistry.kt:176)
    at com.android.tools.lint.client.api.JarFileIssueRegistry$Factory.get(JarFileIssueRegistry.kt:143)
    at com.android.tools.lint.client.api.LintDriver.registerCustomDetectors(LintDriver.kt:426)
    at com.android.tools.lint.client.api.LintDriver.analyze(LintDriver.kt:357)
    at com.android.tools.lint.LintCliClient.run(LintCliClient.java:155)
 */

public class Issues {
  private Issues() {}

  public static Issue ISSUE_MODULE_USAGE = Issue.create(
      "RequiredMethodNotCalled",
      "A required method from this module was not called.",
      "Please visit the class for this field to ensure that all `@RequiredMethod`s are called",
      Category.CORRECTNESS, 8, Severity.FATAL,
      new Implementation(ModuleUsageDetector.class, Scope.JAVA_FILE_SCOPE));
}
