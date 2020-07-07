# Style module

The style module is dealing with the styles of a document.
For example you might want to have a special style for headlines using another font or color.
Or you want to have a 2-column layout instead of the default 1-column layout.

That is done using a style file basically JSON.

## Style file

### File extension

The file extension of a style file is `*.tds` as an abbreviation of *Thaw document style*.

### Syntax

You can style predefined elements like the document layout with something like:

```json
{
	"DOCUMENT": {
		"size": {
			"width": 210,
			"height": 297
		},
		"layout": {
			"type": "column",
			"columns": 2
		},
		"insets": {
			"top": 10,
			"bottom": 10,
			"left": 20,
			"right": 20
		},
		"background": {
			"color": {
				"red": 1.0,
				"green": 1.0,
				"blue": 1.0,
				"alpha": 1.0
			}
		},
		"font": {
			"family": "Cambria",
			"size": 14,
			"color": {
				"red": 0.9,
				"green": 0.9,
				"blue": 0.9
			}
		}
	}
}
```

For every Thingy definition we can set a style:

```json
{
    "H1": {
        "font": {
            "size": 24
        }
    }
}
```
