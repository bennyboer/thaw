# Style module

The style module is dealing with the styles of a document.
For example you might want to have a special style for headlines using another font or color.
Or you want to have a 2-column layout instead of the default 1-column layout.

That is done using a style file that is kind of a merge of JSON and CSS although document-related.

## Style file

### File extension

The file extension of a style file is `*.tds` as an abbreviation of *Thaw document style*.

### Syntax

The syntax is as mentioned a merge of JSON and CSS.
You can style predefined elements like the document layout with something like:

```
DOC {
    layout: {
        type: 'column', // A column layout
        columns: 2, // Two-column layout
    },
    size: { // This is an example for an A4 sized document
        width: 210,
        height: 297
    },
    insets: { // Page insets
        top: 10,
        bottom: 10,
        left: 20,
        right: 20
    },
    background: { // Background of the document
        color: white
    },
    font: { // Default font
        family: 'Calibri',
        size: 14, // 14pt
        color: rgb(0.9, 0.9, 0.9)
    }
}
```

For every Thingy definition we can set a style:

```
H1 {
    font: {
        size: 24 // 24pt
    }
}
```
