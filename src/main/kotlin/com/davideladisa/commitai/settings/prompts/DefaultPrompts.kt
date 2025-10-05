package com.davideladisa.commitai.settings.prompts

private const val HINT_PLACEHOLDER = "{Use this hint to improve the commit message: \$hint}\n"
private const val DIFF_PLACEHOLDER = "{diff}"

enum class DefaultPrompts(val prompt: Prompt) {

    // Generate UUIDs for game objects in Mine.py and call the function in start_game().
    BASIC(
        Prompt(
            name = "Basic",
            description = "Basic prompt that generates a decent commit message.",
            content = "Write an insightful but concise Git commit message in a complete sentence in present tense for the " +
                    "following diff without prefacing it with anything, the response must be in the language {locale} and must " +
                    "NOT be longer than 74 characters. The sent text will be the differences between files, where deleted lines " +
                    "are prefixed with a single minus sign and added lines are prefixed with a single plus sign.\n" +
                    HINT_PLACEHOLDER +
                    DIFF_PLACEHOLDER,
            canBeChanged = true,
            isDefault = true,
            originalContent = "Write an insightful but concise Git commit message in a complete sentence in present tense for the " +
                    "following diff without prefacing it with anything, the response must be in the language {locale} and must " +
                    "NOT be longer than 74 characters. The sent text will be the differences between files, where deleted lines " +
                    "are prefixed with a single minus sign and added lines are prefixed with a single plus sign.\n" +
                    HINT_PLACEHOLDER +
                    DIFF_PLACEHOLDER
        )
    ),

    // feat: generate unique UUIDs for game objects on Mine game start
    CONVENTIONAL(
        Prompt(
            name = "Conventional",
            description = "Prompt for commit message in the conventional commit convention.",
            content = "Generate a conventional commit message with title and body. Follow this format:\n\n" +
                    "type(scope): short description\n\n" +
                    "Longer explanation of what changed and why. Wrap at 72 characters.\n\n" +
                    "Rules:\n" +
                    "- Title: type(scope): description (max 74 chars, present tense)\n" +
                    "- Types: feat, fix, refactor, docs, style, test, chore, perf, ci, build\n" +
                    "- Body: explain what and why, not how (wrap at 72 chars)\n" +
                    "- Use {locale} language\n" +
                    "- Add issue from branch {branch} if available\n" +
                    "- NO words like 'commit', 'message', 'change'\n\n" +
                    "Example:\n" +
                    "refactor(auth): extract user validation into separate service\n\n" +
                    "Move validation logic from UserController to UserValidationService\n" +
                    "to improve code reusability and testability. This reduces coupling\n" +
                    "between the controller and business logic.\n\n" +
                    HINT_PLACEHOLDER +
                    DIFF_PLACEHOLDER,
            canBeChanged = true,
            isDefault = true,
            originalContent = "Generate a conventional commit message with title and body. Follow this format:\n\n" +
                    "type(scope): short description\n\n" +
                    "Longer explanation of what changed and why. Wrap at 72 characters.\n\n" +
                    "Rules:\n" +
                    "- Title: type(scope): description (max 74 chars, present tense)\n" +
                    "- Types: feat, fix, refactor, docs, style, test, chore, perf, ci, build\n" +
                    "- Body: explain what and why, not how (wrap at 72 chars)\n" +
                    "- Use {locale} language\n" +
                    "- Add issue from branch {branch} if available\n" +
                    "- NO words like 'commit', 'message', 'change'\n\n" +
                    "Example:\n" +
                    "refactor(auth): extract user validation into separate service\n\n" +
                    "Move validation logic from UserController to UserValidationService\n" +
                    "to improve code reusability and testability. This reduces coupling\n" +
                    "between the controller and business logic.\n\n" +
                    HINT_PLACEHOLDER +
                    DIFF_PLACEHOLDER
        )
    ),

    // ✨ feat(conditions): add HpComparisonType enum and ICondition interface for unit comparison logic
    EMOJI(
        Prompt(
            name = "GitMoji",
            description = "Prompt for generating commit messages with GitMoji.",
            content = "Generate a GitMoji commit message with title and body. Follow this format:\n\n" +
                    "🔧 type(scope): short description\n\n" +
                    "Longer explanation of what changed and why. Wrap at 72 characters.\n\n" +
                    "Rules:\n" +
                    "- Title: emoji type(scope): description (max 74 chars, present tense)\n" +
                    "- GitMoji: ✨ feat, 🐛 fix, ♻️ refactor, 📝 docs, 💄 style, ✅ test, 🔧 chore, ⚡ perf, 👷 ci, 📦 build\n" +
                    "- Body: explain what and why, not how (wrap at 72 chars)\n" +
                    "- Use {locale} language\n" +
                    "- NO words like 'commit', 'message', 'change'\n\n" +
                    "Example:\n" +
                    "♻️ refactor(auth): extract user validation into separate service\n\n" +
                    "Move validation logic from UserController to UserValidationService\n" +
                    "to improve code reusability and testability. This reduces coupling\n" +
                    "between the controller and business logic.\n\n" +
                    "---\n" +
                    "{diff}",
            canBeChanged = true,
            isDefault = true,
            originalContent = "Generate a GitMoji commit message with title and body. Follow this format:\n\n" +
                    "🔧 type(scope): short description\n\n" +
                    "Longer explanation of what changed and why. Wrap at 72 characters.\n\n" +
                    "Rules:\n" +
                    "- Title: emoji type(scope): description (max 74 chars, present tense)\n" +
                    "- GitMoji: ✨ feat, 🐛 fix, ♻️ refactor, 📝 docs, 💄 style, ✅ test, 🔧 chore, ⚡ perf, 👷 ci, 📦 build\n" +
                    "- Body: explain what and why, not how (wrap at 72 chars)\n" +
                    "- Use {locale} language\n" +
                    "- NO words like 'commit', 'message', 'change'\n\n" +
                    "Example:\n" +
                    "♻️ refactor(auth): extract user validation into separate service\n\n" +
                    "Move validation logic from UserController to UserValidationService\n" +
                    "to improve code reusability and testability. This reduces coupling\n" +
                    "between the controller and business logic.\n\n" +
                    "---\n" +
                    "{diff}"
        )
    );

    companion object {
        fun toPromptsMap(): MutableMap<String, Prompt> {
            return entries.associateBy({ it.prompt.name.lowercase() }, { it.prompt }).toMutableMap()
        }
    }
}
