package time;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class DurationTest {
    @Test
    public void timeUnitCreation() {
        assertEquals(10, Duration.ofDays(10).toDays());
        assertEquals(10, Duration.ofHours(10).toHours());
        assertEquals(10, Duration.ofMinutes(10).toMinutes());
        assertEquals(10, Duration.ofSeconds(10).toSeconds());
        assertEquals(10, Duration.ofMillis(10).toMillis());
        assertEquals(10, Duration.ofNanos(10).toNanos());
    }

    @Test
    public void iso8601TextCreation() {
        assertEquals(Duration.ofDays(1), Duration.parse("P1D"));
        assertEquals(Duration.ofDays(1), Duration.parse("PT24H"));
        assertEquals(Duration.ofHours(1), Duration.parse("PT1H"));
        assertEquals(Duration.ofMinutes(1), Duration.parse("PT1M"));
        assertEquals(Duration.ofSeconds(1), Duration.parse("PT1S"));
        assertEquals(Duration.ofMillis(1), Duration.parse("PT0.001S"));
        assertEquals(Duration.ofNanos(1), Duration.parse("PT0.000000001S"));
        assertEquals(Duration.ofDays(2).plusHours(1).plusMinutes(80).plusSeconds(20).plusMillis(3).plusNanos(900),
                Duration.parse("P2DT1H80M20.003000900S"));
    }

    @Test
    public void durationBetweenTwoInstantsInTime() {
        assertEquals(Duration.ofDays(2).plusHours(1).plusMinutes(1).plusSeconds(1),
                Duration.between(Instant.parse("2000-01-01T00:00:00Z"), Instant.parse("2000-01-03T01:01:01Z")));
        assertEquals(Duration.ofDays(2).plusHours(1).plusMinutes(1).plusSeconds(1),
                Duration.between(
                        LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                        LocalDateTime.of(2000, 1, 3, 1, 1, 1)));
        assertEquals(Duration.ofDays(1).plusHours(17).plusMinutes(1).plusSeconds(1),
                Duration.between(
                        OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2000, 1, 3, 1, 1, 1, 0, ZoneOffset.ofHours(+8))));
        assertEquals(Duration.ofDays(2).plusHours(1).plusMinutes(1).plusSeconds(1),
                Duration.between(
                        LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                        OffsetDateTime.of(2000, 1, 3, 1, 1, 1, 0, ZoneOffset.UTC)));
    }

    @Test
    public void arithmeticOperationsOnTemporals() {
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 5, 0, 0, 0);
        assertEquals(LocalDateTime.of(2000, 1, 6, 0, 0, 0), localDateTime.plus(Duration.ofDays(1)));
        assertEquals(LocalDateTime.of(2000, 1, 4, 0, 0, 0), localDateTime.minus(Duration.ofDays(1)));
        assertEquals(LocalDateTime.of(2000, 1, 5, 0, 0, 1), localDateTime.plus(Duration.ofSeconds(1)));
    }
}
