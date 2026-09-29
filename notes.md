# Truffula Notes
As part of Wave 0, please fill out notes for each of the below files. They are in the order I recommend you go through them. A few bullet points for each file is enough. You don't need to have a perfect understanding of everything, but you should work to gain an idea of how the project is structured and what you'll need to implement. Note that there are programming techniques used here that we have not covered in class! You will need to do some light research around things like enums and and `java.io.File`.

PLEASE MAKE FREQUENT COMMITS AS YOU FILL OUT THIS FILE.

## App.java
- Holds main, the entry point when run from the command line (java src/App.java -nc -h src).
- `main` needs to do three things: build a `TruffulaOptions` from `args`, build a TruffulaPrinter from the options, and call `printTree()`.
- The Javadoc describes the arguments: optional flags `-h` and `-nc`, then a required path.
- main declares `throws Exception` because `TruffulaOptions` can throw `FileNotFoundException`.

## ConsoleColor.java
 enum: a fixed set of named constants. You can't create new ones, and you compare them with `==`.
- Each constant carries a String ANSI escape code, passed to the private constructor ->`RED("\033[0;31m")`.
- `getCode()` returns the code, and `toString()` is overridden to return the code too. So `"" + ConsoleColor.RED` produces the escape sequence directlyy
- `RESET` isn't a real color. It restores the terminal's default color
- `ConsoleColor.values()` returns every constant as an array.

## ColorPrinter.java / ColorPrinterTest.java
Wraps a PrintStream and remembers a currentColor -> (default `WHITE`).
- println(...) variants all into print(String message, boolean reset), and println just appends System.lineSeparator().
- The existing test captures output with a `ByteArrayOutputStream` wrapped in a `PrintStream`, then compares the string exactly. Expected output ->  `COLOR + message + newline + RESET` so the reset comes after the newline.

## TruffulaOptions.java / TruffulaOptionsTest.java

## TruffulaPrinter.java / TruffulaPrinterTest.java

## AlphabeticalFileSorter.java