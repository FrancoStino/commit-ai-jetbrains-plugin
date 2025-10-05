package com.davideladisa.commitai.settings.prompts

private const val HINT_PLACEHOLDER = "{Use this hint to improve the commit message: \$hint}\n"
private const val DIFF_PLACEHOLDER = "{diff}"
private const val BODY_EXPLANATION = "Longer explanation of what changed and why. Wrap at 72 characters.\n\n"
private const val RULES_SECTION = "Rules:\n"
private const val USE_LOCALE_LANGUAGE = "- Use {locale} language\n"
private const val NO_COMMIT_WORDS = "- NO words like 'commit', 'message', 'change'\n\n"
private const val BODY_RULE = "- Body: explain what and why, not how (wrap at 72 chars)\n"
private const val EXAMPLE_SECTION = "Example:\n"
private const val REFACTOR_EXAMPLE_TITLE = "refactor(auth): extract user validation into separate service\n\n"
private const val REFACTOR_EXAMPLE_BODY = "Move validation logic from UserController to UserValidationService\n" +
                                          "to improve code reusability and testability. This reduces coupling\n" +
                                          "between the controller and business logic.\n\n"
private const val GITMO_JI_EXAMPLE_TITLE = "♻️ refactor(auth): extract user validation into separate service\n\n"
private const val TYPES_RULE = "- Types: feat, fix, refactor, docs, style, test, chore, perf, ci, build\n"
private const val TYPE_SCOPE_FORMAT = "type(scope): short description\n\n"
private const val GITMO_JI_TYPE_SCOPE_FORMAT = "🔧 type(scope): short description\n\n"

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
                    TYPE_SCOPE_FORMAT +
                    BODY_EXPLANATION +
                    RULES_SECTION +
                    "- Title: type(scope): description (max 74 chars, present tense)\n" +
                    TYPES_RULE +
                    BODY_RULE +
                    USE_LOCALE_LANGUAGE +
                    "- Add issue from branch {branch} if available\n" +
                    NO_COMMIT_WORDS +
                    EXAMPLE_SECTION +
                    REFACTOR_EXAMPLE_TITLE +
                    REFACTOR_EXAMPLE_BODY +
                    HINT_PLACEHOLDER +
                    DIFF_PLACEHOLDER,
            canBeChanged = true,
            isDefault = true,
            originalContent = "Generate a conventional commit message with title and body. Follow this format:\n\n" +
                    TYPE_SCOPE_FORMAT +
                    BODY_EXPLANATION +
                    RULES_SECTION +
                    "- Title: type(scope): description (max 74 chars, present tense)\n" +
                    TYPES_RULE +
                    BODY_RULE +
                    USE_LOCALE_LANGUAGE +
                    "- Add issue from branch {branch} if available\n" +
                    NO_COMMIT_WORDS +
                    EXAMPLE_SECTION +
                    REFACTOR_EXAMPLE_TITLE +
                    REFACTOR_EXAMPLE_BODY +
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
                    GITMO_JI_TYPE_SCOPE_FORMAT +
                    BODY_EXPLANATION +
                    RULES_SECTION +
                    "- Title: emoji type(scope): description (max 74 chars, present tense)\n" +
                    "- GitMoji: ✨ feat, 🐛 fix, ♻️ refactor, 📝 docs, 💄 style, ✅ test, 🔧 chore, ⚡ perf, 👷 ci, 📦 build\n" +
                    BODY_RULE +
                    USE_LOCALE_LANGUAGE +
                    NO_COMMIT_WORDS +
                    EXAMPLE_SECTION +
                    GITMO_JI_EXAMPLE_TITLE +
                    REFACTOR_EXAMPLE_BODY +
                    "---\n" +
                    DIFF_PLACEHOLDER,
            canBeChanged = true,
            isDefault = true,
            originalContent = "Generate a GitMoji commit message with title and body. Follow this format:\n\n" +
                    GITMO_JI_TYPE_SCOPE_FORMAT +
                    BODY_EXPLANATION +
                    RULES_SECTION +
                    "- Title: emoji type(scope): description (max 74 chars, present tense)\n" +
                    "- GitMoji: ✨ feat, 🐛 fix, ♻️ refactor, 📝 docs, 💄 style, ✅ test, 🔧 chore, ⚡ perf, 👷 ci, 📦 build\n" +
                    BODY_RULE +
                    USE_LOCALE_LANGUAGE +
                    NO_COMMIT_WORDS +
                    EXAMPLE_SECTION +
                    GITMO_JI_EXAMPLE_TITLE +
                    REFACTOR_EXAMPLE_BODY +
                    "---\n" +
                    DIFF_PLACEHOLDER
        )
    );

    companion object {
        fun toPromptsMap(): MutableMap<String, Prompt> {
            return entries.associateBy({ it.prompt.name.lowercase() }, { it.prompt }).toMutableMap()
        }
    }
}
