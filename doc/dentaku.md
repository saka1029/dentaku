# dentaku

## syntax

```
statement       = define-variable
                | define-unary
                | define-binary
                | expression
define-variable = ID '=' expression
define-unary    = ( ID | SPECIAL ) ID '=' expression
define-binary   = ID ( ID | SPECIAL ) ID '=' expression
expression      = sequence { BOP sequence }
sequence        = unary { unary }
unary           = primary
                | UOP unary
                | HOP BOP unary
primary         = '(' expression ')'
                | VAR
                | NUMBER
```

```
ID              = JAVA-ALPHA { JAVA-ALPHA | JAVA-DIGIT}
SPECAIL         = SP { SP }
BOP             = ID | SPECIAL
UOP             = ID | SPECIAL
HOP             = ID | SPECIAL
VAR             = ID
NUMBER = DIGITS
         [ '.' DIGITS ]
         [ ( 'e' | 'E' ) [ '+' | '-' ] DIGITS ]
DIGITS = DIGIT { DIGIT }
DIGIT  = '0' .. '9'
```

```
+ (1 2 3) -> 6
- (1 2 3) -> -1 -2 -3
- (1 -2 3) -> -1 2 -3
* (1 2 3 4) -> 24
```
