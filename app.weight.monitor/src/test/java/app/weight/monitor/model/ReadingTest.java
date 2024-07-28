package app.weight.monitor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReadingTest {

	Reading reading = null;

	@Test
	void testCreate() {
		assertNotNull(new Reading(LocalDate.now(), "22.22"));
	}

	@Test
	void testDate() {
		reading = new Reading(LocalDate.now(), "22.22");
		assertEquals(LocalDate.now(), reading.date());
	}

	@Test
	void testWeight() {
		reading = new Reading(LocalDate.now(), "22.22");
		assertEquals("22.22", reading.weight());
	}
}
