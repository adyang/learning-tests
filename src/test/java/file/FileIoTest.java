package file;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class FileIoTest {
	private static final String FILE_ONE_CONTENTS = "fileOneLine1\nfileOneLine2\n";
	private FileSystem fs;
	private Path fileOne;

	@Before
	public void setUp() throws Exception {
		fs = Jimfs.newFileSystem(Configuration.unix());
		fileOne = fs.getPath("/fileOne.txt");
		Files.write(fileOne, FILE_ONE_CONTENTS.getBytes(StandardCharsets.UTF_8));
	}

	@After
	public void tearDown() throws Exception {
		fs.close();
	}

	@Test
	public void readAllLinesIntoMemory() throws IOException {
		List<String> lines = Files.readAllLines(fileOne, StandardCharsets.UTF_8);
		assertEquals(Arrays.asList("fileOneLine1", "fileOneLine2"), lines);
	}

	@Test
	public void readAllBytesIntoMemory() throws IOException {
		byte[] fileBytes = Files.readAllBytes(fileOne);
		String fileOneContents = new String(fileBytes, StandardCharsets.UTF_8);
		assertEquals(FILE_ONE_CONTENTS, fileOneContents);
	}

	@Test
	public void writeAllLines() throws IOException {
		List<String> lines = Arrays.asList("lineOne", "lineTwo");
		Path file = fs.getPath("/fileToWrite.txt");

		Files.write(file, lines, StandardCharsets.UTF_8);

		assertEquals(lines, Files.readAllLines(file, StandardCharsets.UTF_8));
	}

	@Test
	public void readLinesViaStream() throws IOException {
		try (Stream<String> stream = Files.lines(fileOne, StandardCharsets.UTF_8)) {
			List<String> lines = stream.sorted().collect(Collectors.toList());
			assertEquals(Arrays.asList("fileOneLine1", "fileOneLine2"), lines);
		}
	}

	@Test
	public void readLinesViaBufferedReader() throws IOException {
		String contents = "";
		try (BufferedReader reader = Files.newBufferedReader(fileOne, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null)
				contents += line + "\n";
		}
		assertEquals(FILE_ONE_CONTENTS, contents);
	}

	@Test
	public void writeLinesViaBufferedWriter() throws IOException {
		List<String> lines = Arrays.asList("lineOne", "lineTwo");
		Path file = fs.getPath("/fileToWrite.txt");

		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
		}

		assertEquals(lines, Files.readAllLines(file, StandardCharsets.UTF_8));
	}

	@Test
	public void readFileFromClasspath() throws Exception {
		URL resource = getClass().getClassLoader().getResource("classpath-file.txt");
		Path classpathFile = Paths.get(resource.toURI());
		List<String> lines = Files.readAllLines(classpathFile);
		assertEquals(Arrays.asList("classpathLine1", "classpathLine2"), lines);
	}
}
