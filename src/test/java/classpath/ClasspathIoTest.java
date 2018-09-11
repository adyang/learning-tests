package classpath;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ClasspathIoTest {
    @Test
    public void readAsLineStreamViaBufferedReader() {
        InputStream is = getClass().getResourceAsStream("/classpath-file.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> lines = br.lines().collect(Collectors.toList());

        assertEquals(Arrays.asList("classpathLine1", "classpathLine2"), lines);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_canConvertToStringWithSystemSeparator() {
        InputStream is = getClass().getResourceAsStream("/classpath-file.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String str = br.lines().collect(Collectors.joining(System.lineSeparator()));

        assertEquals("classpathLine1" + System.lineSeparator() + "classpathLine2", str);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_cannotPreserveOriginalSeparator() {
        String unixStr = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/unix-file.txt"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        String windowsStr = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/windows-file.txt"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

        assertEquals("unixLine1\nunixLine2", unixStr);
        assertNotEquals("windowsLine1\r\nwindowsLine2", windowsStr);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_canReadFromJar() {
        List<String> lines = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/jar-file.txt"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("jarLine1","jarLine2"), lines);
    }

    @Test
    public void readAsBytesViaFilesApi() throws URISyntaxException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(getClass().getResource("/classpath-file.txt").toURI()));
        String str = new String(bytes, StandardCharsets.UTF_8);

        assertEquals("classpathLine1\nclasspathLine2", str);
    }

    @Test
    public void readAsBytesViaFilesApi_preservesOriginalSeparator() throws URISyntaxException, IOException {
        String unixStr = new String(Files.readAllBytes(Paths.get(
                getClass().getResource("/unix-file.txt").toURI())), StandardCharsets.UTF_8);
        String windowsStr = new String(Files.readAllBytes(Paths.get(
                getClass().getResource("/windows-file.txt").toURI())), StandardCharsets.UTF_8);

        assertEquals("unixLine1\nunixLine2", unixStr);
        assertEquals("windowsLine1\r\nwindowsLine2", windowsStr);
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void readAsBytesViaFilesApi_cannotReadFromJar() throws URISyntaxException {
        URI uri = getClass().getResource("/jar-file.txt").toURI();
        assertTrue(uri.toString().startsWith("jar:file:"));
        assertTrue(uri.toString().endsWith("jar-file.txt"));

        Paths.get(uri);
    }
}
