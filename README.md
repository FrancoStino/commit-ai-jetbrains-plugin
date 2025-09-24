<div style="text-align: center;">
    <a href="https://plugins.jetbrains.com/plugin/28558-commit-ai/">
        <img src="./src/main/resources/META-INF/commitAi.svg" alt="logo" width="300"/>
    </a>

<h1 style="text-align: center;">Commit AI</h1>
<p style="text-align: center;">Commit AI for IntelliJ based IDEs/Android Studio.</p>

[version-badge]: https://img.shields.io/jetbrains/plugin/28558-commit-ai/v/?style=for-the-badge
[installs-badge]: https://img.shields.io/jetbrains/plugin/28558-commit-ai/d/?style=for-the-badge&color=red
[rating-badge]: https://img.shields.io/jetbrains/plugin/28558-commit-ai/r/stars/?style=for-the-badge
[marketplace-url]: https://plugins.jetbrains.com/plugin/28558-commit-ai/



[![Version][version-badge]][marketplace-url]
[![Installs][installs-badge]][marketplace-url]
[![Rating][rating-badge]][marketplace-url]
</div>
<br>

- [Description](#description)
- [Features](#features)
- [Compatibility](#compatibility)
- [Install](#install)
- [Installation from zip](#installation-from-zip)

<!-- Plugin description -->
## Description

Commit AI is a plugin that generates your commit messages by using git diff and LLMs. To get started, install the
plugin and configure a LLM API client in plugin's settings: <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Commit AI</kbd>
<!-- Plugin description end -->

## Features

- Generate commit message from git diff using LLM
- Compute diff only from the selected files and lines in the commit dialog
- Create your own prompt for commit message generation
- Use predefined variables and hint to customize your prompt
- Supports Git and Subversion as version control systems.

## Supported models

- Groq
- Pollinations

The plugin uses [langchain4j](https://github.com/langchain4j/langchain4j) for creating LLM API clients with OpenAI-compatible APIs. It currently supports Groq and Pollinations. If you would like support for additional LLM providers, please make a feature request in GitHub issues.

## Demo

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="./assets/plugin-dark.gif">
  <source media="(prefers-color-scheme: light)" srcset="./assets/plugin-light.gif">
  <img alt="Demo." src="./assets/plugin-light.gif">
</picture>

## Compatibility

IntelliJ IDEA, PhpStorm, WebStorm, PyCharm, RubyMine, AppCode, CLion, GoLand, DataGrip, Rider, MPS, Android Studio,
DataSpell, Code With Me

## Install

<a href="https://plugins.jetbrains.com/plugin/28558-commit-ai/">
<img alt="Install Plugin" src="https://user-images.githubusercontent.com/12044174/123105697-94066100-d46a-11eb-9832-338cdf4e0612.png"/>
</a>

Or you could install it inside your IDE:

For Windows & Linux: <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search
for "Commit AI"</kbd> > <kbd>Install Plugin</kbd> > <kbd>Restart IntelliJ IDEA</kbd>

For Mac: <kbd>IntelliJ IDEA</kbd> > <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search
for "Commit AI"</kbd> > <kbd>Install Plugin</kbd>  > <kbd>Restart IntelliJ IDEA</kbd>

### Installation from zip

1. Download zip from [releases](https://github.com/FrancoStino/commit-ai-jetbrains-plugin/releases)
2. Import to IntelliJ: <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Cog</kbd> > <kbd>Install plugin from
   disk...</kbd>
3. Set LLM client configuration in plugin's settings: <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Commit AI</kbd>


## Support

* Star the repository
* [Buy me a coffee](https://ko-fi.com/blarc)
* [Rate the plugin](https://plugins.jetbrains.com/plugin/28558-commit-ai/)
* [Share the plugin](https://plugins.jetbrains.com/plugin/28558-commit-ai/)
* [Sponsor me](https://github.com/sponsors/FrancoStino)

## Contributing

Please see [CONTRIBUTING](CONTRIBUTING.md) for details.

## Acknowledgements

- [openai-kotlin](https://github.com/aallam/openai-kotlin) for OpenAI API client.
- [langchain4j](https://github.com/langchain4j/langchain4j) for LLM API clients.

## License

Please see [LICENSE](LICENSE) for details.

## Star History

<a href="https://www.star-history.com/#FrancoStino/commit-ai-jetbrains-plugin&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=FrancoStino/commit-ai-jetbrains-plugin&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=FrancoStino/commit-ai-jetbrains-plugin&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=FrancoStino/commit-ai-jetbrains-plugin&type=Date" />
 </picture>
</a>
