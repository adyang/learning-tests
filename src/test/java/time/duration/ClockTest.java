package time.duration;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class ClockTest {
    private static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.of(2000, 1, 5, 0, 0, 0);
    private Clock clock;

    @Before
    public void setUp() {
        clock = Clock.fixed(CURRENT_DATE_TIME.atOffset(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    }

    @Test
    public void currentInstant() {
        assertEquals(Instant.parse("2000-01-05T00:00:00Z"), clock.instant());
    }

    @Test
    public void currentMillis() {
        assertEquals(CURRENT_DATE_TIME.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(), clock.millis());
    }
}
