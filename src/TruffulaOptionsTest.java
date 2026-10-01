import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TruffulaOptionsTest {

  @Test
  void testValidDirectoryIsSet(@TempDir File tempDir) throws FileNotFoundException {
    // Arrange: Prepare the arguments with the temp directory
    File directory = new File(tempDir, "subfolder");
    directory.mkdir();
    String directoryPath = directory.getAbsolutePath();
    String[] args = {"-nc", "-h", directoryPath};

    // Act: Create TruffulaOptions instance
    TruffulaOptions options = new TruffulaOptions(args);

    // Assert: Check that the root directory is set correctly
    assertEquals(directory.getAbsolutePath(), options.getRoot().getAbsolutePath());
    assertTrue(options.isShowHidden());
    assertFalse(options.isUseColor());
  }

  @Test
void testDefaultsWhenNoFlagsGiven(@TempDir File tempDir) throws FileNotFoundException {
  // Arrange: Only a path, no flags
  String[] args = {tempDir.getAbsolutePath()};

  // Act: Create TruffulaOptions instance
  TruffulaOptions options = new TruffulaOptions(args);

  // Assert: Hidden files off - color on
  assertFalse(options.isShowHidden());
  assertTrue(options.isUseColor());
}

@Test
void testFlagOrderDoesNotMatter(@TempDir File tempDir) throws FileNotFoundException {
  // Arrange: The same flags in two different orders
  String path = tempDir.getAbsolutePath();
  String[] argsA = {"-h", "-nc", path};
  String[] argsB = {"-nc", "-h", path};

  // Act
  TruffulaOptions optionsA = new TruffulaOptions(argsA);
  TruffulaOptions optionsB = new TruffulaOptions(argsB);

  // Assert
  assertTrue(optionsA.isShowHidden());
  assertFalse(optionsA.isUseColor());
  assertTrue(optionsB.isShowHidden());
  assertFalse(optionsB.isUseColor());
}

@Test
void testMissingPathThrows() {
  // Arrange
  String[] args = {"-h"};

  // Act and Assert
  assertThrows(IllegalArgumentException.class, () -> new TruffulaOptions(args));
}

@Test
void testUnknownFlagThrows(@TempDir File tempDir) {
  // Arrange: A flag that doesn't exist
  String[] args = {"-x", tempDir.getAbsolutePath()};

  // Act and Assert
  assertThrows(IllegalArgumentException.class, () -> new TruffulaOptions(args));
}

@Test
void testNonexistentPathThrows(@TempDir File tempDir) {
  // Arrange
  String[] args = {new File(tempDir, "nope").getAbsolutePath()};

  // Act and Assert
  assertThrows(FileNotFoundException.class, () -> new TruffulaOptions(args));
}
}
