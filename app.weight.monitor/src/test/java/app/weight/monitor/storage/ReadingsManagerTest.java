package app.weight.monitor.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.weight.monitor.BaseTest;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.notification.NotificationMonitor;

class ReadingsManagerTest extends BaseTest {

	@BeforeEach
	void setUp() throws Exception {
		resetFlags();
		ApplicationDefinition app = new ApplicationDefinition("test") {
			@Override
			public Level level() {
				return Level.OFF;
			}
		};
		ApplicationConfiguration.registerApplication(app, rootDirectory.getAbsolutePath());
		LogConfigurer.setUp();
		NotificationCentre.addListener(listener);
		if (runMonitor)
			new NotificationMonitor(System.out);
	}

	@AfterEach
	void tearDown() throws Exception {
		clearReadings();
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
		NotificationCentre.clear();
	}

	@Test
	void testNotNull() {
		assertNotNull(ReadingsManager.instance());
	}

	@Test
	void testAddAREading() throws Exception {
		assertEquals(0, ReadingsManager.instance().readings().size());
		addAReading();
		assertEquals(1, ReadingsManager.instance().readings().size());
	}

	@Test
	void testClear() throws Exception {
		assertEquals(0, ReadingsManager.instance().readings().size());
		addAReading();
		assertEquals(1, ReadingsManager.instance().readings().size());
		clearReadings();
		assertEquals(0, ReadingsManager.instance().readings().size());
	}

	@Test
	void testCount() throws Exception {
		assertEquals(0, ReadingsManager.instance().countEntries(LocalDate.now()));
		addAReading();
		assertEquals(1, ReadingsManager.instance().countEntries(LocalDate.now()));
		addAReading();
		assertEquals(2, ReadingsManager.instance().countEntries(LocalDate.now()));
	}

	@Test
	void testEntriesFor() throws Exception {
		assertEquals(0, ReadingsManager.instance().readingsFor(LocalDate.now()).length);
		addAReading();
		assertEquals(1, ReadingsManager.instance().readingsFor(LocalDate.now()).length);
		addAReading();
		assertEquals(2, ReadingsManager.instance().readingsFor(LocalDate.now()).length);
		addAReading(LocalDate.now().plusDays(1), "77");
		assertEquals(3, ReadingsManager.instance().readings().size());
		assertEquals(2, ReadingsManager.instance().readingsFor(LocalDate.now()).length);
	}
}
