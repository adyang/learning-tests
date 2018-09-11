package file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class PathApiTest {
	private FileSystem fs;
	private Path path;

	@Before
	public void setUp() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		path = fs.getPath("/partOne/partTwo/partThree");
	}

	@After
	public void tearDown() throws Exception {
		fs.close();
	}

	@Test
	public void defaultsToNativeFileSystemRepresentation() {
		assertEquals(Paths.get("/some/path"), FileSystems.getDefault().getPath("/some/path"));
		if (isWindowsOs())
			assertEquals("\\some\\path", Paths.get("/some/path").toString());
		else
			assertEquals("/some/path", Paths.get("/some/path").toString());
	}

	private boolean isWindowsOs() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	@Test
	public void createPathFromParts() {
		assertEquals("/partOne/partTwo/partThree", fs.getPath("/partOne", "partTwo", "partThree").toString());
	}

	@Test
	public void pathInformation_fileName() {
		assertPath("partThree", path.getFileName());
	}

	@Test
	public void pathInformation_nameByIndex() {
		assertPath("partOne", path.getName(0));
		assertPath("partTwo", path.getName(1));
		assertPath("partThree", path.getName(2));
		assertEquals(3, path.getNameCount());
	}

	@Test
	public void pathInformation_subPath() {
		assertPath("partOne", path.subpath(0, 1));
		assertPath("partOne/partTwo", path.subpath(0, 2));
		assertPath("partOne/partTwo/partThree", path.subpath(0, 3));
		assertPath("partTwo/partThree", path.subpath(1, 3));
		assertPath("partTwo", path.subpath(1, 2));
	}

	@Test
	public void pathInformation_parent() {
		assertPath("/partOne/partTwo", path.getParent());
	}

	@Test
	public void pathInformation_root() {
		assertPath("/", path.getRoot());
		assertNull(fs.getPath("relative/path").getRoot());
	}

	@Test
	public void normalizePath() {
		assertPath("/partOne/partThree", fs.getPath("/partOne/partTwo/../partThree").normalize());
		assertPath("/partOne/partTwo", fs.getPath("/partOne/./partTwo").normalize());
	}

	@Test(expected = NoSuchFileException.class)
	public void nonExistentFileCheck() throws Exception {
		Path nonExistentPath = fs.getPath("/non/existent/path");
		nonExistentPath.toRealPath();
	}

	@Test
	public void joinPaths() {
		assertPath("/partOne/partTwo", fs.getPath("/partOne").resolve("partTwo"));
		assertPath("/newPartOne/newPartTwo", fs.getPath("/partOne").resolve("/newPartOne/newPartTwo"));
	}

	@Test
	public void pathBetweenTwoPaths_independentNodes() {
		Path pathOne = fs.getPath("siblingOne");
		Path pathTwo = fs.getPath("siblingTwo");
		assertPath("../siblingTwo", pathOne.relativize(pathTwo));
		assertPath("../siblingOne", pathTwo.relativize(pathOne));
	}

	@Test
	public void pathBetweenTwoPaths_sharedNodes() {
		Path pathOne = fs.getPath("sharedParent");
		Path pathTwo = fs.getPath("sharedParent/childOne/childTwo");
		assertPath("childOne/childTwo", pathOne.relativize(pathTwo));
		assertPath("../..", pathTwo.relativize(pathOne));
	}

	@Test
	public void pathComparison() {
		assertTrue(path.startsWith("/partOne"));
		assertTrue(path.endsWith("partThree"));
	}

	private void assertPath(String expectedPathName, Path actualPath) {
		assertEquals(fs.getPath(expectedPathName), actualPath);
	}
}
