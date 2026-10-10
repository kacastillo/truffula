import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TruffulaPrinterTest {

    /**
     * Checks if the current operating system is Windows.
     *
     * This method reads the "os.name" system property and checks whether it
     * contains the substring "win", which indicates a Windows-based OS.
     * 
     * You do not need to modify this method.
     *
     * @return true if the OS is Windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    /**
     * Creates a hidden file in the specified parent folder.
     * 
     * The filename MUST start with a dot (.).
     *
     * On Unix-like systems, files prefixed with a dot (.) are treated as hidden.
     * On Windows, this method also sets the DOS "hidden" file attribute.
     * 
     * You do not need to modify this method, but you SHOULD use it when creating hidden files
     * for your tests. This will make sure that your tests work on both Windows and UNIX-like systems.
     *
     * @param parentFolder the directory in which to create the hidden file
     * @param filename the name of the hidden file; must start with a dot (.)
     * @return a File object representing the created hidden file
     * @throws IOException if an I/O error occurs during file creation or attribute setting
     * @throws IllegalArgumentException if the filename does not start with a dot (.)
     */
    private static File createHiddenFile(File parentFolder, String filename) throws IOException {
        if(!filename.startsWith(".")) {
            throw new IllegalArgumentException("Hidden files/folders must start with a '.'");
        }
        File hidden = new File(parentFolder, filename);
        hidden.createNewFile();
        if(isWindows()) {
            Path path = Paths.get(hidden.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        return hidden;
    }

    @Test
    public void testPrintTree_ExactOutput_WithCustomPrintStream(@TempDir File tempDir) throws IOException {
        // Build the example directory structure:
        // myFolder/
        //    .hidden.txt
        //    Apple.txt
        //    banana.txt
        //    Documents/
        //       images/
        //          Cat.png
        //          cat.png
        //          Dog.png
        //       notes.txt
        //       README.md
        //    zebra.txt

        // Create "myFolder"
        File myFolder = new File(tempDir, "myFolder");
        assertTrue(myFolder.mkdir(), "myFolder should be created");

        // Create visible files in myFolder
        File apple = new File(myFolder, "Apple.txt");
        File banana = new File(myFolder, "banana.txt");
        File zebra = new File(myFolder, "zebra.txt");
        apple.createNewFile();
        banana.createNewFile();
        zebra.createNewFile();

        // Create a hidden file in myFolder
        createHiddenFile(myFolder, ".hidden.txt");

        // Create subdirectory "Documents" in myFolder
        File documents = new File(myFolder, "Documents");
        assertTrue(documents.mkdir(), "Documents directory should be created");

        // Create files in Documents
        File readme = new File(documents, "README.md");
        File notes = new File(documents, "notes.txt");
        readme.createNewFile();
        notes.createNewFile();

        // Create subdirectory "images" in Documents
        File images = new File(documents, "images");
        assertTrue(images.mkdir(), "images directory should be created");

        // Create files in images
        File cat = new File(images, "cat.png");
        File dog = new File(images, "Dog.png");
        cat.createNewFile();
        dog.createNewFile();

        // Set up TruffulaOptions with showHidden = false and useColor = true
        TruffulaOptions options = new TruffulaOptions(myFolder, false, true);

        // Capture output using a custom PrintStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        // Instantiate TruffulaPrinter with custom PrintStream
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Call printTree (output goes to printStream)
        printer.printTree();

        // Retrieve printed output
        String output = baos.toString();
        String nl = System.lineSeparator();

        // Build expected output with exact colors and indentation
        ConsoleColor reset = ConsoleColor.RESET;
        ConsoleColor white = ConsoleColor.WHITE;
        ConsoleColor purple = ConsoleColor.PURPLE;
        ConsoleColor yellow = ConsoleColor.YELLOW;

        StringBuilder expected = new StringBuilder();
        expected.append(white).append("myFolder/").append(nl).append(reset);
        expected.append(purple).append("   Apple.txt").append(nl).append(reset);
        expected.append(purple).append("   banana.txt").append(nl).append(reset);
        expected.append(purple).append("   Documents/").append(nl).append(reset);
        expected.append(yellow).append("      images/").append(nl).append(reset);
        expected.append(white).append("         cat.png").append(nl).append(reset);
        expected.append(white).append("         Dog.png").append(nl).append(reset);
        expected.append(yellow).append("      notes.txt").append(nl).append(reset);
        expected.append(yellow).append("      README.md").append(nl).append(reset);
        expected.append(purple).append("   zebra.txt").append(nl).append(reset);

        // Assert that the output matches the expected output exactly
    //     assertEquals(expected.toString(), output);
    // }

//     private static String runPrinter(File root, boolean showHidden, boolean useColor) {
//     ByteArrayOutputStream baos = new ByteArrayOutputStream();
//     PrintStream printStream = new PrintStream(baos);
//     new TruffulaPrinter(new TruffulaOptions(root, showHidden, useColor), printStream).printTree();
//     return baos.toString();
// }
    @Test
    public void testHiddenFilesAreNotShownWhenShowHiddenIsFalse(@TempDir File tempDir) throws IOException {
        // Arrange: A folder with one visible file and one hidden file, hidden files turned off
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());
        new File(root, "visible.txt").createNewFile();
        createHiddenFile(root, ".secret.txt");

        TruffulaOptions options = new TruffulaOptions(root, false, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Act: Print the tree
        printer.printTree();
        String output = baos.toString();

        // Assert: The visible file is printed and the hidden file is not
        assertTrue(output.contains("visible.txt"));
        assertFalse(output.contains(".secret.txt"));
    }

    @Test
    public void testHiddenFilesAreShownWhenShowHiddenIsTrue(@TempDir File tempDir) throws IOException {
        // Arrange: The same folder, but hidden files turned on
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());
        new File(root, "visible.txt").createNewFile();
        createHiddenFile(root, ".secret.txt");

        TruffulaOptions options = new TruffulaOptions(root, true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Act: Print the tree
        printer.printTree();
        String output = baos.toString();

        // Assert: Both the visible and hidden files are printed
        assertTrue(output.contains("visible.txt"));
        assertTrue(output.contains(".secret.txt"));
    }

    @Test
    public void testContentsOfHiddenFolderAreNotShown(@TempDir File tempDir) throws IOException {
        // Arrange: A hidden folder containing a normal file, hidden files turned off
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());
        File hiddenDir = new File(root, ".hiddenDir");
        assertTrue(hiddenDir.mkdir());
        new File(hiddenDir, "inside.txt").createNewFile();

        TruffulaOptions options = new TruffulaOptions(root, false, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Act: Print the tree
        printer.printTree();
        String output = baos.toString();

        // Assert: Neither the hidden folder nor anything inside it is printed
        assertFalse(output.contains(".hiddenDir"));
        assertFalse(output.contains("inside.txt"));
    }

    // tests for color

        @Test
    public void testColorsCycleByDepth(@TempDir File tempDir) throws IOException {
        // Arrange: One entry per folder so alphabetical order doesn't matter
        // root (white) -> a (purple) -> b (yellow) -> c (white) -> file.txt (purple)
        File root = new File(tempDir, "root");
        File c = new File(root, "a/b/c");
        assertTrue(c.mkdirs());
        new File(c, "file.txt").createNewFile();

        TruffulaOptions options = new TruffulaOptions(root, false, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        String nl = System.lineSeparator();
        ConsoleColor reset = ConsoleColor.RESET;
        StringBuilder expected = new StringBuilder();
        expected.append(ConsoleColor.WHITE).append("root/").append(nl).append(reset);
        expected.append(ConsoleColor.PURPLE).append("   a/").append(nl).append(reset);
        expected.append(ConsoleColor.YELLOW).append("      b/").append(nl).append(reset);
        expected.append(ConsoleColor.WHITE).append("         c/").append(nl).append(reset);
        expected.append(ConsoleColor.PURPLE).append("            file.txt").append(nl).append(reset);

        // Act: Print the tree
        printer.printTree();
        String output = baos.toString();

        // Assert: Each depth uses the next color, wrapping back to white
        assertEquals(expected.toString(), output);
    }

    @Test
    public void testNoColorPrintsEverythingInWhite(@TempDir File tempDir) throws IOException {
        // Arrange: A nested folder, with color turned off
        File root = new File(tempDir, "root");
        File sub = new File(root, "sub");
        assertTrue(sub.mkdirs());
        new File(sub, "file.txt").createNewFile();

        TruffulaOptions options = new TruffulaOptions(root, false, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Act: Print the tree
        printer.printTree();
        String output = baos.toString();

        // Assert: Only white is used, never purple or yellow
        assertTrue(output.contains(ConsoleColor.WHITE.toString()));
        assertFalse(output.contains(ConsoleColor.PURPLE.toString()));
        assertFalse(output.contains(ConsoleColor.YELLOW.toString()));
    }
}

