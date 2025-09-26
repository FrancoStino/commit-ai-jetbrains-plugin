package com.davideladisa.commitai

import com.davideladisa.commitai.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

class CommitAICheckingHandlerFactory : CheckinHandlerFactory() {
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return object : CheckinHandler() {
            override fun beforeCheckin(): ReturnResult {
                panel.project.service<ProjectSettings>().getActiveLLMClientConfiguration()?.getGenerateCommitMessageJob()?.cancel()
                return super.beforeCheckin()
            }
        }
    }
}
