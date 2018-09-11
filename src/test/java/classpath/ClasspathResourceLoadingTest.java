package classpath;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClasspathResourceLoadingTest {
    @Test
    public void loadFromRootOfClasspath() throws IOException, URISyntaxException {
        URL classResource = getClass().getResource("/classpath-root.txt");
        URL classLoaderResource = getClass().getClassLoader().getResource("classpath-root.txt");

        assertEquals("i am at classpath root", readString(classResource));
        assertEquals("i am at classpath root", readString(classLoaderResource));
    }

    @Test
    public void loadFromInnerClasspath() throws IOException, URISyntaxException {
        URL classResource = getClass().getResource("inner/classpath-inner.txt");
        URL classLoaderResource = getClass().getClassLoader().getResource("classpath/inner/classpath-inner.txt");

        assertEquals("i am at classpath/inner", readString(classResource));
        assertEquals("i am at classpath/inner", readString(classLoaderResource));
    }

    @Test
    public void loadUsingClassloader_cannotContainRootSlash() {
        URL classLoaderResource = getClass().getClassLoader().getResource("/classpath-root.txt");

        assertNull(classLoaderResource);
    }

    private String readString(URL resource) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(resource.toURI())), StandardCharsets.UTF_8);
    }
}
