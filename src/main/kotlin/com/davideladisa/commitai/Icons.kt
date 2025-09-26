package com.davideladisa.commitai

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import javax.swing.Icon

object Icons {

    data class CommitAIIcon(val bright: String, val dark: String?) {

        fun getThemeBasedIcon(): Icon {
            return if (JBColor.isBright() || dark == null) {
                IconLoader.getIcon(bright, javaClass)
            } else {
                IconLoader.getIcon(dark, javaClass)
            }
        }
    }

    val AI_COMMITS = CommitAIIcon("/META-INF/pluginIcon.svg", null)
    val POLLINATIONS = CommitAIIcon("/icons/pollinations.svg", null)
    val GROQ = CommitAIIcon("/icons/groq.svg", null)

    object Process {
        val STOP = CommitAIIcon("/icons/stop.svg", "/icons/stop_dark.svg")
    }
}
