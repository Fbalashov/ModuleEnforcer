# Module Enforcer

[![Travis-ci](https://api.travis-ci.org/Fbalashov/ModuleEnforcer.svg)]((https://travis-ci.org/Fbalashov/ModuleEnforcer))
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fbalashov/ModuleEnforcer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fbalashov/ModuleEnforcer)
![Built with Kotlin](https://img.shields.io/badge/built%20with-Kotlin-orange.svg)

<p align="center">
    <img src="moduleEnforcer.png" alt="Module Enforcer Logo" width="150" height="150"/>
</p>

*Composition in Android is hard, this library is meant to make it a bit safer. "How?" you ask, by the
glory of lint rules of course!*

When you compose logic in a class, you have the expectation that developers will use it a certain way.
If you are the only dev, you can guarantee the class (module) will be used correctly. But if you are on a
team or sharing your module, other developers can miss something essential.

Module Enforcer eases the cognitive load for users of your module. You annotate methods that must be called
for the module to work correctly and android lint will warn your users if they are not calling all the annotated methods.

This is especially helpful if you are creating a module that needs to be called for different lifecycle events.
Rather than creating a class that users of your library must extend, you can compose your logic with the assurance
that developers will be guided towards its proper usage.

#### Note

* ModuleEnforcer does work with injected fields.
* Module Enforcer works for both Kotlin and Java
* Currently this library is in development and has some [limitations described below](#limitations).
* The library is subject to breaking changes until the annotation names are settled.

## Sample Output
Command Line Lint Output

![Module Enforcer Lint Output](moduleEnforcer-LintOutput.png "Module Enforcer Lint Output")

IDE "Analyze > Inspect Code" output

![Module Enforcer IDE Inpsection Output](moduleEnforcer-IDEInspection.png "Module Enforcer IDE Inpsection Output")

## Usage

1) Add this library in your `build.gradle`
```
dependencies {
  ...
  compile 'com.github.fbalashov.moduleEnforcer:lib:0.1'
  ...
}
```
2) annotate the module class with `@Module`
3) annotate the needed methods with `@RequiredMethod`
4) try instantiating the module in an activity.
5) Run lint on your project. Oh my, lint errors!
5) call the reported methods in your class, the lint errors will be gone the next time you run lint.

#### Example Module

```
package moduleEnforcer.example

import com.fbalashov.moduleEnforcer.annotations.Module
import com.fbalashov.moduleEnforcer.annotations.RequiredMethod

@Module
class Example Module {
  @RequiredMethod
  fun aFunction() {
    // If you don't call me, you will get a lint error!
  }
  fun bFunction() {
    // I can be ignored :(
  }
}
```

## Limitations
Currently, this library only works in the command line and using the "Analyze>Inspect Code" tool in Android Studio.
It does not highlight modules in the Android Studio IDE while you are coding. This seems to be a discrepancy in how
Android Studio runs lint, but I still need confirmation.

## Areas for Improvement
* Add support for overloaded required methods.
* Come up with a name for @Module that doesn't clash with Dagger's @Module annotation.
* Verify that a method is called in ALL conditions (maybe there is an if statement that prevents a method from being called in certain cases.
* Follow object instances if they are passed into another class so you can assert if the method is called there.

## Contributions
* I am open to any suggestions and feedback you may have. Please open an issue if you have improvements you'd like to share!
* I welcome contributions the community may have! Please open an issue with a description of your improvement and some use cases, we can take it from there.
  * I recommend reading the guide by Niklas Baudy listed below, it will ramp you up on writing lint rules.
* Please keep in mind that my goal is to keep this tool simple and flexible while providing safety for modules.

## Shout-outs
* Thanks to Niklas Baudy for the [guide](https://medium.com/@vanniktech/writing-your-first-lint-check-39ad0e90b9e6)
and [sample lint rules](https://github.com/vanniktech/lint-rules/) he made publicly available.
These proved invaluable in my hunt for educational materials on lint.

## Details
* Modules that have `@RequiredMethods` with optional arguments (a kotlin only construct) will not expect you to
call all variants of the required method. As long as the user of the module calls one form of the method it will not warn them.
* Modules that have overloaded methods will not warn the user if they call at least one of the methods. I consider this wrong,
but have not yet fixed [the issue](https://github.com/Fbalashov/ModuleEnforcer/issues/3).

