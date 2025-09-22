package com.davideladisa.commitai.listeners

import com.davideladisa.commitai.AICommitsBundle
import com.davideladisa.commitai.notifications.Notification
import com.davideladisa.commitai.notifications.sendNotification
import com.davideladisa.commitai.settings.AppSettings2
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ApplicationStartupListener : ProjectActivity {

    private var firstTime = true
    override suspend fun execute(project: Project) {
        showVersionNotification(project)
    }
    private fun showVersionNotification(project: Project) {
        val settings = AppSettings2.instance
        val version = AICommitsBundle.plugin()?.version

        if (version == settings.lastVersion) {
            return
        }

        settings.lastVersion = version
        if (firstTime && version != null) {
            sendNotification(Notification.welcome(version), project)
        }
        firstTime = false
    }
}
