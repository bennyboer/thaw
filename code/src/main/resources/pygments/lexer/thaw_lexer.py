from pygments.lexer import RegexLexer, bygroups
from pygments.token import *
import re

class ThawLexer(RegexLexer):
    name = 'Thaw'
    aliases = ['thaw']
    filenames = ['*.thaw']

    flags = re.DOTALL

    tokens = {
        'root': [
            (r'\\.', String.Escape, 'escaped'),
            (r'\*', Generic.Strong, 'italic-or-bold-start'),
            (r'_', Generic.Emph, 'underlined'),
            (r'`', String.Backtick, 'inline-code'),
            (r'#', Keyword, 'thingy-start'),
            (r'.', Text),
        ],
        'italic-or-bold-start': [
            (r'\*', Generic.Strong, ('#pop', 'bold')),
            (r'.', Generic.Emph, ('#pop', 'italic')),
        ],
        'italic-leave-or-bold-start': [
            (r'\*', Generic.Strong, ('#pop', 'bold')),
            (r'.', Generic.Emph, '#pop:2'),
        ],
        'italic': [
            (r'\\.', String.Escape, 'escaped'),
            (r'\*', Generic.Strong, 'italic-leave-or-bold-start'),
            (r'_', Generic.Emph, 'underlined'),
            (r'`', String.Backtick, 'inline-code'),
            (r'#', Keyword, 'thingy-start'),
            (r'.', Generic.Emph),
        ],
        'bold': [
            (r'\\.', String.Escape, 'escaped'),
            (r'\*', Generic.Strong, 'bold-leave-or-italic-start'),
            (r'_', Generic.Emph, 'underlined'),
            (r'`', String.Backtick, 'inline-code'),
            (r'#', Keyword, 'thingy-start'),
            (r'.', Generic.Strong),
        ],
        'bold-leave-or-italic-start': [
            (r'\*', Generic.Strong, '#pop:2'),
            (r'.', Generic.Strong, ('#pop', 'italic')),
        ],
        'underlined': [
            (r'\\', String.Escape, 'escaped'),
            (r'\*', Generic.Strong, 'italic-or-bold-start'),
            (r'`', String.Backtick, 'inline-code'),
            (r'_', Generic.Emph, '#pop'),
            (r'#', Keyword, 'thingy-start'),
            (r'.', Generic.Emph),
        ],
        'inline-code': [
            (r'\\', String.Escape, 'escaped'),
            (r'`', String.Backtick, '#pop'),
            (r'.', String.Backtick),
        ],
        'thingy-opt-args': [
            (r'\\', String.Escape, 'escaped'),
            (r'#', Keyword, '#pop'),
            (r'"', String.Double, 'double-quote-string'),
            (r"'", String.Single, 'single-quote-string'),
            (r'=', Operator),
            (r',', Operator),
            (r'\n', Generic.Emph),
            (r'\r', Generic.Emph),
            (r'.', Generic.Emph),
        ],
        'thingy-start': [
            (r',', Operator, ('#pop', 'thingy-opt-args')),
            (r'#', Keyword, '#pop'),
            (r'.', Generic.Strong),
        ],
        'double-quote-string': [
            (r'\\', String.Escape, 'escaped'),
            (r'"', String.Double, '#pop'),
            (r'.', String.Double),
        ],
        'single-quote-string': [
            (r'\\', String.Escape, 'escaped'),
            (r"'", String.Single, '#pop'),
            (r'.', String.Single),
        ],
        'escaped': [
            (r'.', String.Escape, '#pop')
        ]
    }
