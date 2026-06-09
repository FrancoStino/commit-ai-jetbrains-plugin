import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask.FailureLevel

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
}

val pluginGroup: String by project
val pluginVersion: String by project
val platformVersion: String by project
val platformPlugins: String by project

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.opentest4j)
    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion")) {
            useInstaller = false
        }

        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.tasks")
        platformPlugins.split(",").map { it.trim() }.forEach { bundledPlugin(it) }
    }
    implementation(libs.langchain4j.openai)
    implementation(libs.langchain4j)
}

intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }
        val changelog = project.changelog 
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
        }
    }
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }
    pluginVerification {
        // PluginManagerCore.getPlugin(PluginId) became @ApiStatus.Internal in build 262 and
        // has no public replacement yet, so exclude INTERNAL_API_USAGES while keeping all
        // real compatibility checks (default set minus INTERNAL_API_USAGES).
        failureLevel = listOf(
            FailureLevel.COMPATIBILITY_PROBLEMS,
            FailureLevel.OVERRIDE_ONLY_API_USAGES,
        )
        ides {
            recommended()
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "251"
                untilBuild = "251.*"
            }
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                channels = listOf(ProductRelease.Channel.EAP)
                sinceBuild = "261"
                untilBuild = "261.*"
            }
        }
    }
}

changelog {
    groups.empty()
}
