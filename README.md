# Module Enforcer
[![Travis-ci](https://api.travis-ci.org/Fbalashov/ModuleEnforcer.svg)]((https://travis-ci.org/Fbalashov/ModuleEnforcer))
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fbalashov/ModuleEnforcer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fbalashov/ModuleEnforcer)

<img src="moduleEnforcer.png" alt="Module Enforcer Logo" width=150px height=150px/>

Currently only supports Java, due to [Android Lint Limitations](#Limitations)

Have you ever looked at an activity and thought: "why is there all this code here?". Maybe you took a step to
move it into neat little abstract activities.
The same problem applies to any classes that you extend from in android: fragments, services, broadcast receivers.

Of course the answer to that is composition, right?
You may have tried it out and quickly realized, "wow, that was a horrible idea, look how many activity
callbacks I had to hook this standalone class into! How will I ever keep this straight?" Then you found a
library that allows you to

Composition in android is hard and this library/lint rule is meant to make it a bit easier. How? Well by the
glory of annotations of course! To make it easy to create/use modules and then implement them properly we will
now annotate methods in a module that need to be called by anything that instantiates that module.

Principle Assumptions:
1) you give your methods in your module names that match the activity/fragment methods they should be called in,
why? Because otherwise it will be challenging for people to figure out where they should be called.
2) you are instantiating your module in your activity? (If this doesn't work for you please take a look and
comment in this issue, PRs are welcome!)
3) You are running an android project with lint turned on <___ maybe???___>

Usage Steps:
0) Add this library in your `build.gradle`
```
dependencies {
  ...
  compile 'com.github.fbalashov.moduleEnforcer:lib:0.0.4-SNAPSHOT'
  ...
}
```
1) annotate the module class with @Module
2) annotate the needed methods with @RequiredMethod
try instantiating the module in an activity and see what happens! Oh my lint errors
3) call the reported methods in your class and you are set.

(Note! If you only need the lifecycle callbacks from an activity you should look into using the
Lifecycle Observer (___insert link____). It is provided by android, doesn't require extending a new base
activity and will call annotated lifecycle callbacks in your module on its own.
However it wont give you anything other than lifecycle callbacks.)

## Limitations
Currently, this library only supports modules and module users written in Java.
This is due to limitations with lint support in Kotlin.
* [As noted in their docs](https://developer.android.com/studio/preview/kotlin-issues.html)
* [And in this issue](https://youtrack.jetbrains.com/issue/KT-7729)

## Areas for Improvement:
*include a method name that the method should be called in as an annotation argument, eg:
```
@requiredMethod('onResume')
```
* Assert that a method is called in ALL conditions (maybe there is an if statement that prevents a method from being called in onResume in certain cases.
* Follow object instances if they are passed into another class so you can assert if the method is called there (Can contradict point 1)

## Attributions
* Thanks to Niklas Baudy for the [guide](https://medium.com/@vanniktech/writing-your-first-lint-check-39ad0e90b9e6)
and [sample lint rules](https://github.com/vanniktech/lint-rules/) he made publicly available.
These proved invaluable in my hunt for educational materials.
*

