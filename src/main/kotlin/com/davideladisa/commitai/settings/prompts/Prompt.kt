package com.davideladisa.commitai.settings.prompts

data class Prompt(
        var name: String = "",
        var description: String = "",
        var content: String = "",
        var canBeChanged: Boolean = true,
        var isDefault: Boolean = false,
        var originalContent: String = ""
) {
    fun isModifiedFromDefault(): Boolean = isDefault && content != originalContent
    
    fun deepCopy(): Prompt = Prompt(
        name = this.name,
        description = this.description,
        content = this.content,
        canBeChanged = this.canBeChanged,
        isDefault = this.isDefault,
        originalContent = this.originalContent
    )
}
