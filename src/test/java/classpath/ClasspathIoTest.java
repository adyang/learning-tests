package classpath;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ClasspathIoTest {
    @Test
    public void readAsByteArrayOutputStream_beforeJava9() throws IOException {
        InputStream is = getClass().getResourceAsStream("/classpath-file.txt");

        String str = readAsByteArrayOutputStream(is);

        assertEquals("classpathLine1\nclasspathLine2", str);
    }

    @Test
    public void readAsByteArrayOutputStreamViaTransferToInJava9() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getClass().getResourceAsStream("/classpath-file.txt").transferTo(os);

        assertEquals("classpathLine1\nclasspathLine2", os.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void readAsByteArrayOutputStream_canReadFromJar() throws IOException {
        InputStream is = getClass().getResourceAsStream("/jar-file.txt");

        String str = readAsByteArrayOutputStream(is);

        assertEquals("jarLine1\njarLine2", str);
    }

    @Test
    public void readAsByteArrayOutputStream_preservesOriginalSeparator() throws IOException {
        InputStream unixStream = getClass().getResourceAsStream("/unix-file.txt");
        InputStream windowsStream = getClass().getResourceAsStream("/windows-file.txt");

        String unixStr = readAsByteArrayOutputStream(unixStream);
        String windowsStr = readAsByteArrayOutputStream(windowsStream);

        assertEquals("unixLine1\nunixLine2", unixStr);
        assertEquals("windowsLine1\r\nwindowsLine2", windowsStr);
    }

    private String readAsByteArrayOutputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) os.write(buffer, 0, length);
        return os.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void readAsLineStreamViaBufferedReader() {
        InputStream is = getClass().getResourceAsStream("/classpath-file.txt");

        List<String> lines = readAsLineStream(is).collect(Collectors.toList());

        assertEquals(Arrays.asList("classpathLine1", "classpathLine2"), lines);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_canConvertToStringWithSystemSeparator() {
        InputStream is = getClass().getResourceAsStream("/classpath-file.txt");

        String str = readAsLineStream(is).collect(Collectors.joining(System.lineSeparator()));

        assertEquals("classpathLine1" + System.lineSeparator() + "classpathLine2", str);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_cannotPreserveOriginalSeparator() {
        InputStream unixStream = getClass().getResourceAsStream("/unix-file.txt");
        InputStream windowsStream = getClass().getResourceAsStream("/windows-file.txt");

        String unixStr = readAsLineStream(unixStream).collect(Collectors.joining(System.lineSeparator()));
        String windowsStr = readAsLineStream(windowsStream).collect(Collectors.joining(System.lineSeparator()));

        assertEquals("unixLine1\nunixLine2", unixStr);
        assertNotEquals("windowsLine1\r\nwindowsLine2", windowsStr);
    }

    @Test
    public void readAsLineStreamViaBufferedReader_canReadFromJar() {
        InputStream is = getClass().getResourceAsStream("/jar-file.txt");

        List<String> lines = readAsLineStream(is).collect(Collectors.toList());

        assertEquals(Arrays.asList("jarLine1", "jarLine2"), lines);
    }

    private Stream<String> readAsLineStream(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        return br.lines();
    }

    @Test
    public void readAsBytesViaFilesApi() throws URISyntaxException, IOException {
        URI resourceUri = getClass().getResource("/classpath-file.txt").toURI();

        String str = readAsBytes(resourceUri);

        assertEquals("classpathLine1\nclasspathLine2", str);
    }

    @Test
    public void readAsBytesViaFilesApi_preservesOriginalSeparator() throws URISyntaxException, IOException {
        URI unixResourceUri = getClass().getResource("/unix-file.txt").toURI();
        URI windowsResourceUri = getClass().getResource("/windows-file.txt").toURI();

        String unixStr = readAsBytes(unixResourceUri);
        String windowsStr = readAsBytes(windowsResourceUri);

        assertEquals("unixLine1\nunixLine2", unixStr);
        assertEquals("windowsLine1\r\nwindowsLine2", windowsStr);
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void readAsBytesViaFilesApi_cannotReadFromJar() throws URISyntaxException, IOException {
        URI uri = getClass().getResource("/jar-file.txt").toURI();

        assertTrue(uri.toString().startsWith("jar:file:"));
        assertTrue(uri.toString().endsWith("jar-file.txt"));

        readAsBytes(uri);
    }

    private String readAsBytes(URI resourceUri) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(resourceUri));
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
