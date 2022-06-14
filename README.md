# Git Version Gradle Script

A small script that integrates [GitVersion](https://gitversion.net) with Gradle.

## Quick Start

1. Apply the script to your project
    - Option 1: Directly from GitHub
    ```kotlin
    apply(from = "https://raw.githubusercontent.com/jfaixo/git-version-gradle-script/main/gitversion.gradle.kts")
    ```
    - Option 2: By copying the script inside your project
    ```kotlin
    apply(from = "<path to your file>/gitversion.gradle.kts")
    ```
    > If you have a multi-module project, apply the script in the buildscript of the root project, this way the CLI download logic will only trigger once
2. Use it inside your module:
```kotlin
val getGitVersionData: () -> Map<String, Any> by extra
val gitVersionData = getGitVersionData()
println(gitVersionData["FullSemVer"] as String)
```

A full documentation of all the available fields is available [here](https://gitversion.net/docs/reference/variables). GitVersion can also be configured, please read their [great documentation](https://gitversion.net/docs/) :)
## Examples

### Android
Automatically generate the version code of the app based on GitVersion
```kotlin
// versionCode = XXXYYYZZZ with X.Y.Z being major, minor and patch
// Max allowed value of versionCode = 2000000000 (corresponding to a version 2000.000.000)

versionCode = gitVersionData["Major"] as Int * 1000000 + gitVersionData["Minor"] as Int * 1000 + gitVersionData["Patch"] as Int
versionName = gitVersionData["FullSemVer"] as String
```

### Application Gradle Plugin
Inject the FullSemVer version inside the codebase so that it can be for example printed.

[See an example here](examples/gradle-app/build.gradle.kts)

## How it works

When you apply the script, if not present the CLI binary of GitVersion is downloaded, depending on your current OS (works with Linux, Windows, MacOS). An extra function is also injected in each module & project that allows to invoke GitVersion and get the parsed output data.

The binary is stored inside your `GRADLE_USER_HOME`, inside a folder tree dedicated to it (something like `gitversion/<version number>/gitversion`), making it cache friendly for CI systems.
