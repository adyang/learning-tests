package collection;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class MapTest {
    @Test
    public void singletonMapCreation() {
        Map<String, String> singletonMap = Collections.singletonMap("key", "value");

        assertEquals(1, singletonMap.size());
        assertEquals("value", singletonMap.get("key"));
    }

    @Test
    public void mutableMapCreation() {
        Map<String, String> mutableMap = new HashMap<>();
        mutableMap.put("keyOne", "valueOne");
        mutableMap.put("keyTwo", "valueTwo");

        assertEquals(2, mutableMap.size());
        assertEquals("valueOne", mutableMap.get("keyOne"));
        assertEquals("valueTwo", mutableMap.get("keyTwo"));
    }

    @Test
    public void streamEntriesMapCreation() {
        Map<String, String> streamMap = Stream.of(
                new SimpleImmutableEntry<>("keyOne", "valueOne"),
                new SimpleImmutableEntry<>("keyTwo", "valueTwo"))
                .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue));

        assertEquals(2, streamMap.size());
        assertEquals("valueOne", streamMap.get("keyOne"));
        assertEquals("valueTwo", streamMap.get("keyTwo"));
    }

    @Test
    public void java9MapCreation() {
        Map<String, String> java9Map = Map.of(
                "keyOne", "valueOne",
                "keyTwo", "valueTwo");

        assertEquals(2, java9Map.size());
        assertEquals("valueOne", java9Map.get("keyOne"));
        assertEquals("valueTwo", java9Map.get("keyTwo"));
    }

    @Test
    public void vavrMapCreation() {
        Map<String, String> vavrJavaMap = io.vavr.collection.HashMap.of(
                "keyOne", "valueOne",
                "keyTwo", "valueTwo")
                .toJavaMap();

        assertEquals(2, vavrJavaMap.size());
        assertEquals("valueOne", vavrJavaMap.get("keyOne"));
        assertEquals("valueTwo", vavrJavaMap.get("keyTwo"));
    }

    @Test
    public void guavaMapCreation() {
        Map<String, String> guavaMap = ImmutableMap.of(
                "keyOne", "valueOne",
                "keyTwo", "valueTwo");

        assertEquals(2, guavaMap.size());
        assertEquals("valueOne", guavaMap.get("keyOne"));
        assertEquals("valueTwo", guavaMap.get("keyTwo"));
    }
}
