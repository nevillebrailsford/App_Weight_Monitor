package app.weight.monitor.storage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import app.weight.monitor.model.Reading;
import application.definition.ApplicationConfiguration;
import application.storage.AbstractStoreData;

/**
 * Write the current history of readings to the backing store.
 */
public class ReadingsStore extends AbstractStoreData {
	private static final String CLASS_NAME = ReadingsStore.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	@Override
	public void storeData() throws IOException {
		LOGGER.entering(CLASS_NAME, "storeData");
		try (PrintWriter archive = new PrintWriter(new BufferedWriter(new FileWriter(fileName())))) {
			writeDataTo(archive);
		} catch (Exception e) {
			IOException exc = new IOException("PropertyStore: Exception occurred - " + e.getMessage(), e);
			LOGGER.throwing(CLASS_NAME, "storeData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "storeData");
		}
	}

	private void writeDataTo(PrintWriter archive) throws IOException {
		LOGGER.entering(CLASS_NAME, "writeDataTo");
		for (Reading reading : ReadingsManager.instance().readings()) {
			archive.println(reading.date().format(dateFormatter) + "     " + reading.weight());
		}
		LOGGER.exiting(CLASS_NAME, "writeDate");
	}
}