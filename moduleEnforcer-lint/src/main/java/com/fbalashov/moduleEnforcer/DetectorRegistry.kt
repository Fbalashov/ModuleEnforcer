package com.fbalashov.moduleEnforcer

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.fbalashov.moduleEnforcer.Issues.ISSUE_MODULE_USAGE

/**
 * @author Fuad.Balashov on 2/19/2018.
 */
class DetectorRegistry: IssueRegistry() {
  override val issues: List<Issue>
    get() = listOf(ISSUE_MODULE_USAGE)
  override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}