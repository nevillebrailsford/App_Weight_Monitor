package app.weight.monitor.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
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

class ReadingsLoadTest extends BaseTest {

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
		readingsLoad = new ReadingsLoad();
		modelFile = new File(rootDirectory, "model.dat");
	}

	@AfterEach
	void tearDown() throws Exception {
		clearReadings();
		ApplicationConfiguration.clear();
		LogConfigurer.shutdown();
		NotificationCentre.clear();
	}

	@Test
	void testEmptyFile() throws Exception {
		assertFalse(loadSuccess);
		List<String> lines = Arrays.asList("");
		createEntriesInFile(lines);
		loadData();
		assertTrue(loadSuccess);
		assertEquals(0, ReadingsManager.instance().readings().size());
	}

	@Test
	void testOneEntry() throws Exception {
		assertFalse(loadSuccess);
		List<String> lines = Arrays.asList("2024/08/22 88");
		createEntriesInFile(lines);
		loadData();
		assertTrue(loadSuccess);
		assertEquals(1, ReadingsManager.instance().readings().size());
		assertEquals("2024/08/22", ReadingsManager.instance().readings().get(0).date().format(dateFormatter));
		assertEquals("88", ReadingsManager.instance().readings().get(0).weight());
	}

	@Test
	void testServeralEntries() throws Exception {
		assertFalse(loadSuccess);
		List<String> lines = Arrays.asList("2024/08/22 88", "2024/08/23 77", "2024/08/24 99");
		createEntriesInFile(lines);
		loadData();
		assertTrue(loadSuccess);
		assertEquals(3, ReadingsManager.instance().readings().size());
	}

	private void loadData() throws IOException, Exception {
		readingsLoad.setFileName(modelFile.getAbsolutePath());
		Storage storage = new Storage();
		synchronized (waitForFinish) {
			storage.loadStoredData(readingsLoad);
			waitForFinish.wait();
		}
	}

	private void createEntriesInFile(List<String> lines) throws Exception {
		assertFalse(modelFile.exists());
		Files.write(modelFile.toPath(), lines);
		assertTrue(modelFile.exists());
		assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
	}

}
