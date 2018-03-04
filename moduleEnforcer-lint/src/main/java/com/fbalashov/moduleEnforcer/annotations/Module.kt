package com.fbalashov.moduleEnforcer.annotations

/**
 * @author Fuad.Balashov on 2/11/2018.
 *
 * Used to modularize code that lived in activities and fragments.
 * In combination with the @RequiredMethod annotation, you can mark methods in classes that must be called
 * by any class that holds an instance of the module.
 *
 * Usage:
 * Any class that you turn into a module must be annotated with the @ModuleModel annotation.
 * Methods in the class that must be called for the module to function properly, should be annotated
 * with the @RequiredMethod annotation.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Module