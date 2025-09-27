package com.davideladisa.commitai

import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.AppSettings2
import com.davideladisa.commitai.settings.ProjectSettings
import com.intellij.DynamicBundle
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.OneTimeString
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.service
import com.intellij.openapi.diff.impl.patch.IdeaTextPatchBuilder
import com.intellij.openapi.diff.impl.patch.UnifiedDiffWriter
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.tasks.TaskManager
import com.intellij.util.text.DateFormatUtil
import com.intellij.vcsUtil.VcsUtil
import git4idea.GitVcs
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.idea.svn.SvnUtil
import org.jetbrains.idea.svn.SvnVcs
import java.io.StringWriter
import java.nio.file.FileSystems
import java.util.*

object CommitAIUtils {

    fun isCommitDiffEmpty(project: Project, commitWorkflowHandler: com.intellij.vcs.commit.AbstractCommitWorkflowHandler<*, *>?): Boolean {
        if (commitWorkflowHandler == null) return true
        
        try {
            val includedChanges = commitWorkflowHandler.ui.getIncludedChanges()
            
            // Quick check: if no changes are included, diff is empty
            if (includedChanges.isEmpty()) return true
            
            // Check if all changes are excluded by our filters
            val hasValidChanges = includedChanges.any { change ->
                change.filePath()?.path?.let { path ->
                    !isPathExcluded(path, project)
                } ?: false
            }
            
            return !hasValidChanges
            
        } catch (e: Exception) {
            // If we can't determine, assume there might be changes to be safe
            return false
        }
    }

    fun isPathExcluded(path: String, project: Project): Boolean {
        return AppSettings2.instance.isPathExcluded(path) || project.service<ProjectSettings>().isPathExcluded(path)
    }

    fun matchesGlobs(text: String, globs: Set<String>): Boolean {
        val fileSystem = FileSystems.getDefault()
        for (globString in globs) {
            val glob = fileSystem.getPathMatcher("glob:$globString")
            if (glob.matches(fileSystem.getPath(text))) {
                return true
            }
        }
        return false
    }

    fun constructPrompt(promptContent: String, diff: String, branch: String?, hint: String?, project: Project): String {
        var content = promptContent
        val locale = project.service<ProjectSettings>().locale
        content = content.replace("{locale}", locale.getDisplayLanguage(Locale.ENGLISH))
        content = replaceBranch(content, branch)
        content = replaceHint(content, hint)

        // TODO @FrancoStino: If TaskManager is null, the prompt might be incorrect...
        TaskManager.getManager(project)?.let {
            val activeTask = it.activeTask
            content = content.replace("{taskId}", activeTask.id)
            content = content.replace("{taskSummary}", activeTask.summary)
            content = content.replace("{taskDescription}", activeTask.description.orEmpty())
            content = content.replace("{taskTimeSpent}", DateFormatUtil.formatTime(activeTask.totalTimeSpent))
        }

        return if (content.contains("{diff}")) {
            content.replace("{diff}", diff)
        } else {
            "$content\n$diff"
        }
    }

    fun replaceBranch(promptContent: String, branch: String?): String {
        if (promptContent.contains("{branch}")) {
            if (branch != null) {
                return promptContent.replace("{branch}", branch)
            } else {
                sendNotification(Notification.noCommonBranch())
                return promptContent.replace("{branch}", "main")
            }
        }
        return promptContent
    }

    fun replaceHint(promptContent: String, hint: String?): String {
        val hintRegex = Regex("\\{[^{}]*(\\\$hint)[^{}]*}")

        hintRegex.find(promptContent, 0)?.let {
            if (!hint.isNullOrBlank()) {
                var hintValue = it.value.replace("\$hint", hint)
                hintValue = hintValue.replace("{", "")
                hintValue = hintValue.replace("}", "")
                return promptContent.replace(it.value, hintValue)
            } else {
                return promptContent.replace(it.value, "")
            }
        }
        return promptContent.replace("{hint}", hint.orEmpty())
    }

    suspend fun getCommonBranch(changes: List<Change>, project: Project): String? {
        return withContext(Dispatchers.IO) {
            val branches = mutableListOf<String>()
            for (change in changes) {
                val virtualFile = change.virtualFile ?: continue
                val vcs = VcsUtil.getVcsFor(project, virtualFile) ?: continue
                val branch: String? = when {
                    isSvnAvailable() && vcs is SvnVcs -> {
                        SvnUtil.getUrl(vcs, VfsUtilCore.virtualToIoFile(virtualFile))?.let { url ->
                            extractSvnBranchName(url.toDecodedString())
                        }
                    }

                    vcs is GitVcs -> {
                        GitRepositoryManager.getInstance(project)
                            .getRepositoryForFile(virtualFile)
                            ?.currentBranchName
                    }

                    else -> null
                }
                if (branch != null) {
                    branches.add(branch)
                }
            }
            branches.groupingBy<String, String> { it }.eachCount().maxByOrNull { it.value }?.key
        }
    }

    fun computeDiff(
        includedChanges: List<Change>,
        reversePatch: Boolean,
        project: Project
    ): String {
        // go through included changes, create a map of repository to changes and discard nulls
        val changesByRepository = includedChanges
            .filter {
                it.filePath()?.path?.let { path ->
                    !isPathExcluded(path, project)
                } ?: false
            }
            .mapNotNull { change ->
                change.filePath()?.let { filePath ->
                    VcsUtil.getVcsRootFor(project, filePath)?.let { vcsRoot ->
                        vcsRoot to change
                    }
                }
            }
            .filter { !it.second.isSubmoduleChange(project) }
            .groupBy({ it.first }, { it.second })


        // compute diff for each repository
        return changesByRepository
            .map { (vcsRoot, changes) ->
                val filePatches = IdeaTextPatchBuilder.buildPatch(
                    project,
                    changes,
                    vcsRoot.toNioPath(), reversePatch, true
                )

                val stringWriter = StringWriter()
                stringWriter.write("Repository: ${vcsRoot.path}\n")
                UnifiedDiffWriter.write(project, filePatches, stringWriter, "\n", null)
                stringWriter.toString()
            }
            .joinToString("\n")
    }

    private fun extractSvnBranchName(url: String): String? {
        val normalizedUrl = url.lowercase()

        // Standard SVN layout: repository/trunk, repository/branches/name, repository/tags/name
        return when {
            normalizedUrl.contains("/branches/") -> {
                val branchPart = url.substringAfter("/branches/")
                val endIndex = branchPart.indexOf('/')
                if (endIndex > 0) branchPart.substring(0, endIndex) else branchPart
            }

            normalizedUrl.contains("/tags/") -> {
                val tagPart = url.substringAfter("/tags/")
                val endIndex = tagPart.indexOf('/')
                if (endIndex > 0) "tag: ${tagPart.substring(0, endIndex)}" else "tag: $tagPart"
            }

            normalizedUrl.contains("/trunk") -> "trunk"
            else -> null // fallback: no branch concept available
        }
    }

    suspend fun retrieveToken(title: String): OneTimeString? {
        val credentialAttributes = getCredentialAttributes(title)
        val credentials = withContext(Dispatchers.IO) {
            PasswordSafe.instance.get(credentialAttributes)
        }
        return credentials?.password
    }
//
    fun getCredentialAttributes(title: String): CredentialAttributes {
        return CredentialAttributes(title)
    }

    fun getIDELocale(): Locale {
        return DynamicBundle.getLocale()
    }

    private fun isClassAvailable(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    private fun isSvnAvailable(): Boolean {
        return isClassAvailable("org.jetbrains.idea.svn.SvnVcs") && isClassAvailable("org.jetbrains.idea.svn.SvnUtil")
    }
}
