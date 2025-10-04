# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
#  (2025-10-04)


### Bug Fixes

* Address LLM client synchronization and UI update issues ([e1d264e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/e1d264ecf1018e7bcb90a31fe6d50375cb0781d4))
* Handle TaskManager null case in prompt generation (develop) ([dad7e57](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/dad7e57224cb467935e84355003b2956a2e07b17))
* Refine regex and string manipulation in AICommitsUtils (develop) ([0d7b1c5](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/0d7b1c5ca0da74fea5390dbd57bf9008e02b5076))
* Simplify hint replacement logic ([a8e3bda](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/a8e3bdab4779218919c2ee90c0ac3f5a845e35b0))


### Features

* Add SonarCloud Quality Gate badge to README (develop) ([224eeba](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/224eeba6143b107410fc78c10e55617a61c1ec48))
* Differentiate main and child actions in LLM client wrapper ([4a74e4a](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4a74e4a24ec46f7d59f6808796261dd75a9c7fca))
* Implement extension points for LLM clients (develop) ([8a9c562](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/8a9c562a75c734cca52178d17bf0329bb5f4dc56))
* Increment plugin version to 1.5.1 (develop) ([312a7e2](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/312a7e2be9ebe3a3799d5191e8596fb36445f2c5))
* Remove unused setActiveLlmClient function (develop) ([2caedde](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/2caedde14b49df2270159ac278b909895a866e9a))



#  (2025-10-01)



# [1.5.0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.4.1...1.5.0) (2025-10-01)


### Bug Fixes

* Adjust logo size in README (develop) ([fb28622](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/fb28622e327bb7d91275446f7d15743423b95273))
* Update dependabot target branch to master (develop) ([963f7ab](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/963f7ab93ce292f01273d3e90362a53553f659b3))


### Features

* Improve LLM client settings synchronization and UI updates (develop) ([2bb011d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/2bb011dc11b9b348e00922b25495eaee91aca244))
* Increment plugin version to 1.5.0 (develop) ([37a1c72](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/37a1c729e45bd54e27f9fb88fbde0a825587c14d))
* Refactor LLM client selection and caching (develop) ([97dd548](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/97dd548fd85f443bef61b4479cc42a4accef2ee1))



#  (2025-09-30)


### Features

* **settings:** display client name and model ID in UI presentation ([99a359b](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/99a359b16da67919e39e97a679b837b62f313fd4))
* update for eap ([5485aac](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/5485aac1fe6b47d1da7b677b936a9aed04264849))



#  (2025-09-28)



# [1.4.0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.3.0...1.4.0) (2025-09-28)


### Bug Fixes

* **ci:** replace main to master ([96ab97d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/96ab97d4aa36e59a21a55f689b36ea1bec0e9d03))
* clean commit messages ([eed2818](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/eed281830b82e08093256414d6e6f2f57ef2c22b))
* enable action only ([8718283](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/8718283f8d6f6a82d94cb6355fc01250a4615f11))
* update CHANGELOG.md for released version (master) ([cb8980a](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/cb8980a98cdaf575071cf573b5e55e90890d65fc))


### Features

* **settings:** remove ([851ca59](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/851ca59bb3c5a358f5c227f7b0b7eccbdb7dae87))




### Changed
- Replace "Git diff is empty" alert notifications with UI state management
- Disable and gray out commit actions when no diff is available instead of showing alerts
- Show proper LLM client icons in split button action instead of generic icons
- Improve performance by avoiding heavy diff calculations on EDT thread

### Fixed
- Resolve threading issues that caused IllegalStateException during diff checking
- Fix icon display to show selected LLM client instead of generic round icon


# [1.3.0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.2.1...1.3.0) (2025-09-27)


### Features

* Add free/freemium labels to LLM client list items (develop) ([93141af](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/93141afd1a3900ec0b85b8e7f1a3a54dc069529d))
* Add free/freemium labels to LLM client list items (develop) ([36399f3](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/36399f311863e329efee4c5c7987c51bc7cbc9ff))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([f4f8b2d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/f4f8b2d33d25bdd821730f2206bc9900a61c9ebc))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([4c1b9b6](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4c1b9b6b84fb89b1953048664d78b78b772cc423))
* **ci:** add workflow to sync main and develop branches ([428b015](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/428b0150c2a836d1c4193a403329c4d1dc0c0ac3))
* **settings/prompts:** enhance prompt configuration and editing ([926b65e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/926b65e0e18365cd70a82d03f8a20d929d8b375b))
* **settings/prompts:** enhance prompt configuration and editing ([ad6bb18](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/ad6bb18ebb816a7facba469d5483e1cf39aad095))



#  (2025-09-27)


### Features

* Add free/freemium labels to LLM client list items (develop) ([93141af](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/93141afd1a3900ec0b85b8e7f1a3a54dc069529d))
* Add free/freemium labels to LLM client list items (develop) ([36399f3](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/36399f311863e329efee4c5c7987c51bc7cbc9ff))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([f4f8b2d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/f4f8b2d33d25bdd821730f2206bc9900a61c9ebc))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([4c1b9b6](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4c1b9b6b84fb89b1953048664d78b78b772cc423))
* **ci:** add workflow to sync main and develop branches ([428b015](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/428b0150c2a836d1c4193a403329c4d1dc0c0ac3))
* **settings/prompts:** enhance prompt configuration and editing ([926b65e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/926b65e0e18365cd70a82d03f8a20d929d8b375b))
* **settings/prompts:** enhance prompt configuration and editing ([ad6bb18](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/ad6bb18ebb816a7facba469d5483e1cf39aad095))



## [1.2.1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.2.0...1.2.1) (2025-09-25)



#  (2025-09-27)


### Features

* Add free/freemium labels to LLM client list items (develop) ([93141af](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/93141afd1a3900ec0b85b8e7f1a3a54dc069529d))
* Add free/freemium labels to LLM client list items (develop) ([36399f3](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/36399f311863e329efee4c5c7987c51bc7cbc9ff))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([f4f8b2d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/f4f8b2d33d25bdd821730f2206bc9900a61c9ebc))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([4c1b9b6](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4c1b9b6b84fb89b1953048664d78b78b772cc423))
* **ci:** add workflow to sync main and develop branches ([428b015](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/428b0150c2a836d1c4193a403329c4d1dc0c0ac3))
* **settings/prompts:** enhance prompt configuration and editing ([926b65e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/926b65e0e18365cd70a82d03f8a20d929d8b375b))
* **settings/prompts:** enhance prompt configuration and editing ([ad6bb18](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/ad6bb18ebb816a7facba469d5483e1cf39aad095))



## [1.2.1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.2.0...1.2.1) (2025-09-25)



#  (2025-09-26)


### Features

* Add free/freemium labels to LLM client list items (develop) ([93141af](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/93141afd1a3900ec0b85b8e7f1a3a54dc069529d))
* Add free/freemium labels to LLM client list items (develop) ([36399f3](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/36399f311863e329efee4c5c7987c51bc7cbc9ff))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([f4f8b2d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/f4f8b2d33d25bdd821730f2206bc9900a61c9ebc))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([4c1b9b6](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4c1b9b6b84fb89b1953048664d78b78b772cc423))
* **settings/prompts:** enhance prompt configuration and editing ([926b65e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/926b65e0e18365cd70a82d03f8a20d929d8b375b))
* **settings/prompts:** enhance prompt configuration and editing ([ad6bb18](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/ad6bb18ebb816a7facba469d5483e1cf39aad095))



## [1.2.1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.2.0...1.2.1) (2025-09-25)



#  (2025-09-26)


### Features

* Add free/freemium labels to LLM client list items (develop) ([93141af](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/93141afd1a3900ec0b85b8e7f1a3a54dc069529d))
* **build:** add IntelliJ tasks plugin to bundled dependencies ([f4f8b2d](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/f4f8b2d33d25bdd821730f2206bc9900a61c9ebc))
* **settings/prompts:** enhance prompt configuration and editing ([926b65e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/926b65e0e18365cd70a82d03f8a20d929d8b375b))



## [1.2.1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.2.0...1.2.1) (2025-09-25)



#  (2025-09-25)



# [1.2.0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.1.1...1.2.0) (2025-09-24)


### Bug Fixes

* Restore commit icon in split button ([483a0b5](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/483a0b5f3e324e0f937a91dd3821f9bcbbc4ae7a))
* update pluginVersion to 1.2.0 in gradle.properties (develop) ([3d0f34b](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/3d0f34bff696d58e3de547c292f195f4d1b51f7d))
* Use execute method to avoid OverrideOnly API usage violation ([bfce48b](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/bfce48b47b00f8fd4a293406e57cc484daf41471))


### Features

* add seed model validation to pollinations client (issue #XYZ) ([2de62b4](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/2de62b4904c2091d4859194055a73f4cb4ee1795))
* bump plugin version to 1.2.0 (develop) ([21b90d0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/21b90d021e97349e6c54465d7c83fe5bfedc6118))



#  (2025-09-24)


### Bug Fixes

* Restore commit icon in split button ([483a0b5](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/483a0b5f3e324e0f937a91dd3821f9bcbbc4ae7a))
* update pluginVersion to 1.2.0 in gradle.properties (develop) ([3d0f34b](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/3d0f34bff696d58e3de547c292f195f4d1b51f7d))
* Use execute method to avoid OverrideOnly API usage violation ([bfce48b](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/bfce48b47b00f8fd4a293406e57cc484daf41471))


### Features

* add seed model validation to pollinations client (issue #XYZ) ([2de62b4](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/2de62b4904c2091d4859194055a73f4cb4ee1795))
* bump plugin version to 1.2.0 (develop) ([21b90d0](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/21b90d021e97349e6c54465d7c83fe5bfedc6118))



## [1.1.1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/compare/1.1.0...1.1.1) (2025-09-24)


### Bug Fixes

* update CHANGELOG to reflect latest release details (issue [#1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/issues/1)) ([4dab5f4](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4dab5f4fa50388d947f0c4675076996ddfc43c20))


### Features

* **ci:** add changelog auto-update workflow and conditional build logic ([488422e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/488422e5d6f4a43a711b4a673d5b7a29ebc06c41))
* Enhance LLM config selection in split button (develop) ([adf3a70](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/adf3a705cdea7749048ec2486772df1de8d5ddd0))
* Update plugin icon and README (develop) ([6041b55](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/6041b55ab10676e72bd2c2702855898e78f44b1e))



#  (2025-09-24)


### Bug Fixes

* update CHANGELOG to reflect latest release details (issue [#1](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/issues/1)) ([4dab5f4](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/4dab5f4fa50388d947f0c4675076996ddfc43c20))


### Features

* Add prereleased release type (master) ([ea533a9](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/ea533a9a35004db32718f8a34a6b35cd8665de58))
* **ci:** add changelog auto-update workflow and conditional build logic ([488422e](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/488422e5d6f4a43a711b4a673d5b7a29ebc06c41))
* **commit:** enhance commit flow with detailed validation and improved change retrieval ([5f36b05](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/5f36b05854df6f999bf2ae0d08f525b4b8c419a7))
* Enhance LLM config selection in split button (develop) ([adf3a70](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/adf3a705cdea7749048ec2486772df1de8d5ddd0))
* Remove release-please workflow (main) ([1e3f277](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/1e3f2777f9cfe723c7854bca0a79182a92044bff))
* Update plugin and dependencies (develop) ([7fd3f94](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/7fd3f94bd0dde3779c10a812de439ee5a6e9e91a))
* Update plugin icon and README (develop) ([6041b55](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/6041b55ab10676e72bd2c2702855898e78f44b1e))


### Reverts

* Revert "Update GitHub workflow to rename Plugin Verification task to `verifyPlugin`" ([23f5a79](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/commit/23f5a795ebe545fde9d78b29e21786abe44cfb85))




## [1.1.0] - 2025-09-24

### 🚀 Features
- **Plugin Enhancement**: Update plugin icon and README for better user experience ([6041b55](https://github.com/your-repo/commit/6041b55))
- **UI Improvement**: Enhance LLM config selection in split button interface ([adf3a70](https://github.com/your-repo/commit/adf3a70))
- **Dependencies**: Update plugin and dependencies to latest versions ([7fd3f94](https://github.com/your-repo/commit/7fd3f94))

### 📚 Documentation
- **README**: Update links and badges to reflect new plugin URL ([0189984](https://github.com/your-repo/commit/0189984))

### 🎨 Style
- **Bundle**: Remove redundant semicolon in repository URL ([5545852](https://github.com/your-repo/commit/5545852))

### 🧹 Chore
- **Cleanup**: Remove Kotlin session files ([6340496](https://github.com/your-repo/commit/6340496))

## [1.0.0] - 2025-09-23

### 🚀 Features
- **Commit Flow**: Enhance commit flow with detailed validation and improved change retrieval ([5f36b05](https://github.com/your-repo/commit/5f36b05))

### Added
- Initial release
