# Thaw

*Thaw* is an hierarchical organizable and perfectly versionable document framework with an export to PDF.

## Motivation

Current modern alternatives to TeX/LaTeX and its derivatives include WYSIWYG editors like DTP software which is usually very expensive (Adobe InDesign) or human-readable formats like Markdown that lack a lot of features.
We want to improve the situation by proposing *Thaw* which lets you organize your documents in a human-readable way while being easy to learn and use as well as being suitable for version control software like Git.

## Project structure

The project is organized in multiple modules:

| Module name | Folder | Description |
| --- | --- | --- |
| CLI | `/cli` | Command-line interface for the Thaw project |
| Core | `/core` | The core module containing the document model. |
| Text | `/text` | Text file parsing and model. |
| Style | `/style` | Style file parsing and model. |
| Reference | `/reference` | Reference file parsing and model. |
| Info | `/info` | Document information (meta data, etc.) and model. |
| Typesetting | `/typeset` | Code related to typesetting a document. |
| Export | `/export` | Related to exporting a document (for example to PDF). |
| Plugin | `/plugin` | Plugin development resources. |
