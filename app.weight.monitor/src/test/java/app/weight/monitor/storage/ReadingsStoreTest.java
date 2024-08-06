package app.weight.monitor.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
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
import application.storage.Storage;

class ReadingsStoreTest extends BaseTest {

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
		readingsStore = new ReadingsStore();
		modelFile = new File(rootDirectory, "model.dat");
	}

	@AfterEach
	void tearDown() throws Exception {
		synchronized (waitForFinish) {
			ReadingsManager.instance().clear();
			waitForFinish.wait();
		}
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
		NotificationCentre.clear();
	}

	@Test
	void testStore() throws Exception {
		assertFalse(storeSuccess);
		assertFalse(modelFile.exists());
		Storage storage = initStorage();
		createEntries();
		writeToStore(storage);
		assertTrue(modelFile.exists());
		assertTrue(storeSuccess);
		List<String> lines = Files.readAllLines(modelFile.toPath());
		assertEquals(3, lines.size());
	}

	private void createEntries() throws Exception {
		addAReading();
		addAReading();
		addAReading();
	}

}
