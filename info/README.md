# Info module

The info module defines the thaw info file format which the users can define variables and meta-data with.

## Info file format

It resembles a properties file format.

### File extension

The file extension of the info file format is `*.tdi` which is short for Thaw document info.

### Examples

```
encoding = UTF-8
language = de

author.name = Benjamin Eder
author.email = barny.eder@gmail.com
```

| Key | Description |
| --- | --- |
| `encoding` | The encoding of the text files in the project. Defaults to the Systems standard charset. |
| `language` | Language code of the language the document is written in. Used for hyphenation. |
| `author.name` | The author name of the document. |
| `author.email` | The authors E-mail address. |
