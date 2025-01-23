import org.gradle.api.Project
import java.io.File

// In the upstream KT compiler repo, ".git" is a directory. `Project.removePrePushHookIfExists()` finds
// ".git/hooks/pre-push.sample" and removes it. However, on studio-main `external/jetbrains/kotlin`, we
// link ".git" to a symbolic link "../../../.repo/projects/external/jetbrains/kotlin.git". This script
// recognizes it as a file and fails on `mainRepoPath` search (`require(it.isNotEmpty())` below).
// Since we do not have "hooks/pre-push.sample" under
// "../../../.repo/projects/external/jetbrains/kotlin.git", we simply skip this script.

/*
project.removePrePushHookIfExists()
*/

fun Project.removePrePushHookIfExists() {
    val prePushHookPath = rootProject.getGitDirectory().toPath()
        .resolve("hooks")
        .resolve("pre-push")
    java.nio.file.Files.deleteIfExists(prePushHookPath)
}

fun Project.getGitDirectory(): File {
    val dotGitFile = File(projectDir, ".git")

    return if (dotGitFile.isFile) {
        val workTreeLink = dotGitFile.readLines().single { it.startsWith("gitdir: ") }
        val mainRepoPath = workTreeLink
            .substringAfter("gitdir: ", "")
            .substringBefore("/.git/worktrees/", "")
            .also { require(it.isNotEmpty()) }

        File(mainRepoPath, ".git").also { require(it.isDirectory) }
    } else {
        dotGitFile
    }
}