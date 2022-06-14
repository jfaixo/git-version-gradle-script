/**
 * Git Version gradle script
 *
 * This script exposes a `getGitVersionData` function that allows to easily download and use https://gitversion.net/
 *
 */
import org.codehaus.groovy.runtime.ProcessGroovyMethods
import groovy.json.JsonSlurper

val gitVersionVersion = "5.10.1"

val linuxUrl = "https://github.com/GitTools/GitVersion/releases/download/$gitVersionVersion/gitversion-linux-x64-$gitVersionVersion.tar.gz"
val windowsUrl = "https://github.com/GitTools/GitVersion/releases/download/$gitVersionVersion/gitversion-win-x64-$gitVersionVersion.zip"
val macUrl = "https://github.com/GitTools/GitVersion/releases/download/$gitVersionVersion/gitversion-osx-x64-$gitVersionVersion.tar.gz"
val localGitVersionDirectory = "${gradle.gradleUserHomeDir}/gitversion/$gitVersionVersion/"

val os = System.getProperty("os.name").toLowerCase()
var gitVersionPath = when {
    os.contains("win") -> "${localGitVersionDirectory}gitversion.exe"
    else -> "${localGitVersionDirectory}gitversion"
}

/**
 * Download Git Version for the right operating system, and install it in the gradle user home if this is not already the case
 */
logger.info("[Git Version] Checking if GitVersion ${gitVersionVersion} is installed")
when {
    os.contains("win") -> {
        if (!File(gitVersionPath).exists()) {
            logger.info("[Git Version] Downloading GitVersion $gitVersionVersion for Windows...")
            val temporaryDownloadedFile = "${gradle.gradleUserHomeDir}/gitversion.zip"
            ant.invokeMethod("get", mapOf("src" to windowsUrl, "dest" to File(temporaryDownloadedFile)))
            logger.info("[Git Version] Extracting...")
            File(localGitVersionDirectory).mkdirs()
            ant.invokeMethod("unzip", mapOf("src" to temporaryDownloadedFile, "dest" to localGitVersionDirectory))
            ant.invokeMethod("delete", mapOf("file" to temporaryDownloadedFile))
            logger.info("[Git Version] Gitversion $gitVersionVersion installed")
        }
        else {
            logger.info("[Git Version] GitVersion already installed")
        }
    }
    os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
        if (!File(gitVersionPath).exists()) {
            logger.info("[Git Version] Downloading GitVersion $gitVersionVersion for Linux...")
            val temporaryDownloadedFile = "${gradle.gradleUserHomeDir}/gitversion.tar.gz"
            ant.invokeMethod("get", mapOf("src" to linuxUrl, "dest" to File(temporaryDownloadedFile)))
            logger.info("[Git Version] Extracting...")
            File(localGitVersionDirectory).mkdirs()
            ant.invokeMethod("untar", mapOf("src" to temporaryDownloadedFile, "dest" to localGitVersionDirectory, "compression" to "gzip"))
            ant.invokeMethod("chmod", mapOf("file" to gitVersionPath, "perm" to "755"))
            ant.invokeMethod("delete", mapOf("file" to temporaryDownloadedFile))
            logger.info("[Git Version] Gitversion $gitVersionVersion installed")
        }
        else {
            logger.info("[Git Version] GitVersion already installed")
        }
    }
    os.contains("mac") -> {
        if (!File(gitVersionPath).exists()) {
            logger.info("[Git Version] Downloading GitVersion $gitVersionVersion for Mac...")
            val temporaryDownloadedFile = "${gradle.gradleUserHomeDir}/gitversion.tar.gz"
            ant.invokeMethod("get", mapOf("src" to macUrl, "dest" to File(temporaryDownloadedFile)))
            logger.info("[Git Version] Extracting...")
            File(localGitVersionDirectory).mkdirs()
            ant.invokeMethod("untar", mapOf("src" to temporaryDownloadedFile, "dest" to localGitVersionDirectory, "compression" to "gzip"))
            ant.invokeMethod("chmod", mapOf("file" to gitVersionPath, "perm" to "755"))
            ant.invokeMethod("delete", mapOf("file" to temporaryDownloadedFile))
            logger.info("[Git Version] Gitversion $gitVersionVersion installed")
        }
        else {
            logger.info("[Git Version] GitVersion already installed")
        }
    }
    else -> {
        logger.error("[Git Version] Your platform $os is not supported.")
    }
}

/**
 * Inject the `getGitVersionData` function in each gradle project/modules
 */
allprojects {
    extra.set("getGitVersionData", fun() : Map<String, Any> {
        val output = ProcessGroovyMethods.getText(ProcessGroovyMethods.execute(gitVersionPath))
        return JsonSlurper().parseText(output) as Map<String, Any>
    })
}