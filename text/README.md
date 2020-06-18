# Text module

The text module is dealing with parsing of the text source files of a document.
The files will be parsed to a text model that manages the textual structure of the document.

## Text file format

Here we will define the anticipated text file format.
We aim at having a human-readable format with minimal extra information (Paragraph type, labels, citations, images, tables, lists, ...).

### File extension

Since the projects name is `Thaw` and we are dealing with a text file, the file extension should be `*.ttx`.

### Emphasis

You can add emphasis by formatting the text either bold, italic, underlined or code-like.
The syntax is mostly borrowed from Markdown.

| Type | Syntax |
| --- | --- |
| **Bold** | `**I'm bold!**` |
| **Italic** | `*I'm italic!*` |
| **Underlined** | `_I'm underlined!_` |
| **Code** | `` `I am code!` `` |

### Paragraphs

A paragraph is essentially a block of text enclosed by empty lines (Or the beginning/end of a the file).

```
This is a paragraph!

This is one as well!
I am in the same paragraph as "This is one as well!".
```

### Explicit line breaking

Sometimes you want to have an explicit line break which can be achieved by adding `#BR#` to the document.
Text enclosed in `#` characters is called a *Thingy*.
*Thingys* simply do special stuff like line breaks!

### Headings

Headings are the usual way to structure documents.
In Thaw they are just treated as any other paragraph.

**But** there is one special thing we can do: Styling the heading "paragraphs" for example with another font, color, etc.
Styling won't be documented here but instead in the README of the style module.
Still we can say that special Thingys do have already default styling on them.
There are Thingys for headlines, for example `#H1#` for a first level headline!
That goes down until `#H5#` which is a fifth level headline.

You can use those headline Thingys like this:

```
#H1# First level headline

#H2# Second level headline

#H3# Third level headline

...
```

Thingys can do a lot of stuff!
For the headline Thingys they automatically add a label that can be used to reference that headline in another paragraph using the reference Thingy `#REF, target=first-level-headline#`.
You can set the label by yourself by using `#H1, label=first#` and `#REF, target=first#`.

### Lists

We have only a single type of text that can either be styled like an unordered of an ordered list as you need it (See style module).

```
- First point
- Second point
    - Sub-point of the second point
- Third point
```

### Links

Links need to be explicitely referenced as a link using `#REF, target=https://github.com/bennyboer/thaw, name=link#`.
In the result there will only be shown `link` not the whole URL.

### Escaping

All special characters like `*`, `` ` ``, `_`, `#`, `|` can be escaped by prefixing them with `\`.
For example `\#`.

### Images

```
#IMG, src="path/to/image.pdf", alignment=CENTER, float=NONE, margin=(0, 0, 0, 0), padding=(0,0,0,0), size=(500, 300), label="Test", caption="Hi there this is an image!", offset=(0, 0)#
```

### Tables

```
| Test content 1 | Test content 2 | Test content 3 |
| Hi | there | |
```

Column alignment and styles of the table/table cells can be set using the styles file (See style module).

### Math formulas

Math formulas can be typed using LaTeX syntax and the math Thingy `#MATH#`.

```
I am a paragraph having inline math formulas #MATH, \frac{1}{2}#.

#MATH, \frac{1}{2}, alignment=CENTER#
```
