plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.6.21"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    mavenCentral()
}

// ##################################################
// ########## Here is the interesting part ##########
// ##################################################

apply("https://raw.githubusercontent.com/jfaixo/git-version-gradle-script/main/gitversion.gradle.kts")

// Instanciate the Git Version wrapper
val getGitVersionData: () -> Map<String, Any> by extra
// Call the underlying CLI
val gitVersionData = getGitVersionData()
// Print, just for fun
println(gitVersionData)

// Set the application version to the full SemVer value
version = gitVersionData["FullSemVer"]!!

// ##################################################


application {
    // Define the main class for the application.
    mainClass.set("hello.gitversion.AppKt")
}

// Generate a small file in order to inject our FullSemVer from git version into the codebase
tasks.create("generateVersionNumber") {
    val outputDir = File("$buildDir/generated-src")
    outputs.dir(outputDir)

    doFirst {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        File(outputDir, "Version.kt").writeText("""package hello.gitversion

const val Version : String = "${project.version}"
""")
    }
}

tasks.getByName("compileKotlin") {
    dependsOn("generateVersionNumber")
}

java.sourceSets["main"].java {
    srcDir("$buildDir/generated-src")
}