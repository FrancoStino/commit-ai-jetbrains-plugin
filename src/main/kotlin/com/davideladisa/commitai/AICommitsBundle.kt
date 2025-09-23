package com.davideladisa.commitai

import com.intellij.DynamicBundle
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.net.URI

@NonNls
private const val BUNDLE = "messages.AiCommitsBundle"

object AICommitsBundle : DynamicBundle(BUNDLE) {

    val URL_BUG_REPORT = URI("https://github.com/FrancoStino/commit-ai-jetbrains-plugin/28558-commit-ai--free/issues")
    val URL_PROMPTS_DISCUSSION = URI("https://github.com/FrancoStino/commit-ai-jetbrains-plugin/28558-commit-ai--free/discussions/2")
    val URL_GITHUB = URI("https://github.com/FrancoStino/commit-ai-jetbrains-plugin")
    val URL_KOFI = URI("https://ko-fi.com/davideladisa")
    val URL_GITHUB_SPONSORS = URI("https://github.com/sponsors/FrancoStino")

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("SpreadOperator", "unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)

    fun openPluginSettings(project: Project) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, message("settings.general.group.title"))
    }

    fun openRepository() {
        BrowserLauncher.instance.open("https://github.com/FrancoStino/commit-ai-jetbrains-plugin");
    }

    fun plugin() = PluginManagerCore.getPlugin(PluginId.getId("com.davideladisa.commit-ai"))


}
