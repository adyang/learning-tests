package file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class JavaFilesApiTest {
	private FileSystem fs;
	private Path fileOne;
	private Path dirOne;
	private Path fileTwo;
	private Path nonExistentFile;

	@Before
	public void setUp() throws Exception {
		fs = Jimfs.newFileSystem(Configuration.unix());
		fileOne = fs.getPath("/fileOne.txt");
		dirOne = fs.getPath("/dirOne");
		fileTwo = dirOne.resolve("fileTwo.txt");
		nonExistentFile = fs.getPath("/nonExistentFile");
		Files.createFile(fileOne);
		Files.createDirectory(dirOne);
		Files.createFile(fileTwo);
	}

	@After
	public void tearDown() throws Exception {
		fs.close();
	}

	@Test
	public void fileExistence() throws IOException {
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.notExists(nonExistentFile));
	}

	@Test
	public void fileAccessibility() throws Exception {
		assertTrue(Files.isReadable(fileOne));
		assertTrue(Files.isWritable(fileOne));
		assertTrue(Files.isExecutable(fileOne));
	}

	@Test
	public void checkFileOrDirectory() throws Exception {
		assertTrue(Files.isRegularFile(fileOne));
		assertFalse(Files.isDirectory(fileOne));
		assertTrue(Files.isDirectory(dirOne));
		assertFalse(Files.isRegularFile(dirOne));
	}

	@Test
	public void checkSameFileForDiffPaths() throws Exception {
		Path anotherFileOne = fs.getPath("/fileOne.txt");
		assertTrue(Files.isSameFile(fileOne, anotherFileOne));
	}

	@Test(expected = NoSuchFileException.class)
	public void createDirectoryWithMissingIntermediate_throwsException() throws Exception {
		Files.createDirectory(fs.getPath("/dirOne/nonExistentDir/dirTwo"));
	}

	@Test
	public void createDirectoriesWithMissingIntermediate_succeeds() throws Exception {
		Path dirTwo = fs.getPath("/dirOne/dirTwo");
		Path dirThree = fs.getPath("/dirOne/dirTwo/dirThree");
		assertTrue(Files.notExists(dirTwo));
		assertTrue(Files.notExists(dirThree));

		Files.createDirectories(dirThree);

		assertTrue(Files.exists(dirTwo));
		assertTrue(Files.exists(dirThree));
	}

	@Test
	public void createTemporaryFile_WithPrefixAndSuffix() throws Exception {
		Path tempFile = Files.createTempFile(dirOne, "prefix", ".suffix");
		assertTrue(Files.exists(tempFile));
		assertTrue(tempFile.getFileName().toString().startsWith("prefix"));
		assertTrue(tempFile.getFileName().toString().endsWith(".suffix"));
	}

	@Test
	public void createTemporaryFile_WithoutPrefixAndSuffix() throws Exception {
		Path tempFile = Files.createTempFile(dirOne, null, null);
		assertTrue(Files.exists(tempFile));
		assertTrue(tempFile.getFileName().toString().endsWith(".tmp"));
	}

	@Test
	public void deleteExistingFile() throws Exception {
		assertTrue(Files.exists(fileOne));
		Files.delete(fileOne);
		assertTrue(Files.notExists(fileOne));
	}

	@Test(expected = NoSuchFileException.class)
	public void deleteNonExistentFile_throwsException() throws Exception {
		assertTrue(Files.notExists(nonExistentFile));
		Files.delete(nonExistentFile);
	}

	@Test
	public void deleteIfExists_failsSilently() throws Exception {
		assertTrue(Files.notExists(nonExistentFile));
		Files.deleteIfExists(nonExistentFile);
	}

	@Test(expected = DirectoryNotEmptyException.class)
	public void deleteNonEmptyDirectory_throwsException() throws Exception {
		Path fileInDir = dirOne.resolve("fileInDir");
		Files.createFile(fileInDir);
		assertTrue(Files.exists(fileInDir));
		Files.delete(dirOne);
	}

	@Test
	public void copyFile() throws Exception {
		Path fileOneCopy = dirOne.resolve("fileOneCopy");
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.notExists(fileOneCopy));

		Files.copy(fileOne, fileOneCopy);

		assertTrue(Files.exists(fileOneCopy));
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void copyFile_targetAlreadyExists_throwsException() throws Exception {
		Path fileOneCopy = dirOne.resolve("fileOneCopy");
		Files.createFile(fileOneCopy);
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.exists(fileOneCopy));

		Files.copy(fileOne, fileOneCopy);
	}

	@Test
	public void copyFileWithReplaceExistingOption_targetAlreadyExists_succeeds() throws Exception {
		Path fileOneCopy = dirOne.resolve("fileOneCopy");
		Files.createFile(fileOneCopy);
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.exists(fileOneCopy));

		Files.copy(fileOne, fileOneCopy, StandardCopyOption.REPLACE_EXISTING);

		assertTrue(Files.exists(fileOneCopy));
	}

	@Test
	public void copyDirectory() throws Exception {
		assertTrue(Files.list(dirOne).count() > 0);
		Path dirOneCopy = fs.getPath("/dirOneCopy");
		assertTrue(Files.notExists(dirOneCopy));

		Files.copy(dirOne, dirOneCopy);

		assertTrue(Files.isDirectory(dirOneCopy));
		assertTrue(Files.list(dirOneCopy).count() == 0);
	}

	@Test
	public void moveFile() throws Exception {
		Path fileOneMoved = dirOne.resolve("fileOneMoved");
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.notExists(fileOneMoved));

		Files.move(fileOne, fileOneMoved);

		assertTrue(Files.notExists(fileOne));
		assertTrue(Files.exists(fileOneMoved));
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void moveFile_targetAlreadyExists_throwsException() throws Exception {
		Path fileOneMoved = dirOne.resolve("fileOneMoved");
		Files.createFile(fileOneMoved);
		assertTrue(Files.exists(fileOne));
		assertTrue(Files.exists(fileOneMoved));

		Files.move(fileOne, fileOneMoved);
	}

	@Test
	public void listContentsInDirectory() throws Exception {
		try (Stream<Path> stream = Files.list(dirOne)) {
			List<Path> contents = stream.collect(Collectors.toList());
			assertEquals(1, contents.size());
			assertTrue(contents.contains(fileTwo));
		}
	}

	@Test
	public void findContentsInDirectory() throws Exception {
		try (Stream<Path> stream = Files.find(fs.getPath("/"), 2, (path, attr) -> Files.isRegularFile(path))) {
			List<Path> contents = stream.collect(Collectors.toList());
			assertEquals(2, contents.size());
			assertTrue(contents.contains(fileOne));
			assertTrue(contents.contains(fileTwo));
		}
	}

	@Test
	public void fileTimestampAttributes() throws Exception {
		FileTime creationTime = FileTime.fromMillis(1511633338000L);
		FileTime lastModifiedTime = FileTime.fromMillis(1511892535000L);
		FileTime lastAccessTime = FileTime.fromMillis(1512065335000L);
		Files.getFileAttributeView(fileOne, BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime,
				creationTime);

		assertEquals(creationTime, Files.getAttribute(fileOne, "creationTime"));
		assertEquals(lastModifiedTime, Files.getLastModifiedTime(fileOne));
		assertEquals(lastAccessTime, Files.getAttribute(fileOne, "lastAccessTime"));
	}
}
