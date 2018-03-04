package com.fbalashov.moduleEnforcer.annotations

/**
 * @author Fuad.Balashov on 2/11/2018.
 *
 * Marks required methods in a class as required.
 * Must be used in conjunction with the @Module annotation.
 * Android Lint will throw errors for any class using an
 * @Module without calling all @RequiredMethods.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class RequiredMethod