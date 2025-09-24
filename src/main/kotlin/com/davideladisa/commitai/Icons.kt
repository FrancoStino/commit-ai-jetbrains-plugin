package com.davideladisa.commitai

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import javax.swing.Icon

object Icons {

    data class AICommitsIcon(val bright: String, val dark: String?) {

        fun getThemeBasedIcon(): Icon {
            return if (JBColor.isBright() || dark == null) {
                IconLoader.getIcon(bright, javaClass)
            } else {
                IconLoader.getIcon(dark, javaClass)
            }
        }
    }

    val AI_COMMITS = AICommitsIcon("/META-INF/pluginIcon.svg", null)
    val POLLINATIONS = AICommitsIcon("/icons/pollinations.svg", null)
    val GROQ = AICommitsIcon("/icons/groq.svg", null)

    object Process {
        val STOP = AICommitsIcon("/icons/stop.svg", "/icons/stop_dark.svg")
    }
}
