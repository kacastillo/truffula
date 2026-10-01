import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorPrinterTest {

  @Test
  void testPrintlnWithRedColorAndReset() {
    // Arrange: Capture the printed output
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);
    printer.setCurrentColor(ConsoleColor.RED);

    // Act: Print the message
    String message = "I speak for the trees";
    printer.println(message);


    String expectedOutput = ConsoleColor.RED + "I speak for the trees" + System.lineSeparator() + ConsoleColor.RESET;

    // Assert: Verify the printed output
    assertEquals(expectedOutput, outputStream.toString());
  }


@Test
void testPrintWithReset() {
  // Arrange: Capture the printed output
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);

  ColorPrinter printer = new ColorPrinter(printStream);
  printer.setCurrentColor(ConsoleColor.BLUE);

  // Act: Print the message without a newline
  String message = "hi";
  printer.print(message);

  String expectedOutput = ConsoleColor.BLUE + "hi" + ConsoleColor.RESET;

  // Assert: verify the color code, message, and reset are all printed
  assertEquals(expectedOutput, outputStream.toString());
}

@Test
void testPrintWithoutReset() {
  // Arrange
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);

  ColorPrinter printer = new ColorPrinter(printStream);
  printer.setCurrentColor(ConsoleColor.BLUE);

  // Act
  String message = "hi";
  printer.print(message, false);

  String expectedOutput = ConsoleColor.BLUE + "hi";

  // Assert
  assertEquals(expectedOutput, outputStream.toString());
}

@Test
void testDefaultColorIsWhite() {
  // Arrange
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream);

  // Act
  ColorPrinter printer = new ColorPrinter(printStream);

  // Assert
  assertEquals(ConsoleColor.WHITE, printer.getCurrentColor());
}
}

