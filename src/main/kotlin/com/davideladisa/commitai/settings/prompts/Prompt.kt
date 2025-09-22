package com.davideladisa.commitai.settings.prompts

data class Prompt(
        var name: String = "",
        var description: String = "",
        var content: String = "",
        var canBeChanged: Boolean = true
)
