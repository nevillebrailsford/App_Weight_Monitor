package app.weight.monitor.storage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import app.weight.monitor.model.Reading;
import application.definition.ApplicationConfiguration;
import application.storage.Storage;

/**
 * ReadingManager provides the interface to the backing storage for the weight
 * monitor application.
 */
public class ReadingsManager {
	private static final String CLASS_NAME = ReadingsManager.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private static ReadingsManager instance = null;

	private List<Reading> readings = new ArrayList<>();

	private ReadingsStore readingsStore;
	private File modelDirectory;
	private File dataFile;
	private Storage storage;

	/**
	 * Create an instance of this application, or return the instance if already in
	 * existence.
	 * 
	 * @return the ReadingManager singleton.
	 */
	public synchronized static ReadingsManager instance() {
		LOGGER.entering(CLASS_NAME, "instance");
		if (instance == null) {
			instance = new ReadingsManager();
		}
		LOGGER.exiting(CLASS_NAME, "isnatnce", instance);
		return instance;
	}

	private ReadingsManager() {
		LOGGER.entering(CLASS_NAME, "cinit");
		readingsStore = new ReadingsStore();
		modelDirectory = obtainModelDirectory();
		dataFile = new File(modelDirectory, ModelConstants.READINGS_FILE);
		readingsStore.setFileName(dataFile.getAbsolutePath());
		storage = new Storage();
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	/**
	 * Clear the readings currently held.
	 */
	public void clear() {
		LOGGER.entering(CLASS_NAME, "clear");
		synchronized (readings) {
			readings.clear();
			updateStorage();
		}
		LOGGER.exiting(CLASS_NAME, "clear");
	}

	/**
	 * Add a reading to the currently held readings.
	 * 
	 * @param reading - the reading to be added.
	 */
	public void addReading(Reading reading) {
		LOGGER.entering(CLASS_NAME, "addReading", reading);
		if (reading == null) {
			IllegalArgumentException exc = new IllegalArgumentException("ReadingManager: reading is null");
			updateFailed(exc);
			LOGGER.throwing(CLASS_NAME, "addReading", exc);
			LOGGER.exiting(CLASS_NAME, "addReading");
			throw exc;
		}
		synchronized (readings) {
			readings.add(reading);
			Collections.sort(this.readings);
			updateStorage();
		}
		LOGGER.exiting(CLASS_NAME, "addReading");
	}

	/**
	 * Remove a reading from the currently held readings.
	 * 
	 * @param reading - the reading to be removed.
	 */
	public void deleteReading(Reading reading) {
		LOGGER.entering(CLASS_NAME, "deleteReading", reading);
		if (reading == null) {
			IllegalArgumentException exc = new IllegalArgumentException("ReadingManager: reading is null");
			updateFailed(exc);
			LOGGER.throwing(CLASS_NAME, "deleteReading", exc);
			LOGGER.exiting(CLASS_NAME, "deleteReading");
			throw exc;
		}
		synchronized (readings) {
			readings.remove(reading);
			updateStorage();
		}
		LOGGER.exiting(CLASS_NAME, "deleteReading");
	}

	/**
	 * Initialise is called once to load the initial values.
	 * 
	 * @param readings - a list of readings
	 */
	public void initialise(List<Reading> readings) {
		LOGGER.entering(CLASS_NAME, "");
		synchronized (this.readings) {
			this.readings.addAll(readings);
			Collections.sort(this.readings);
		}
		LOGGER.exiting(CLASS_NAME, "");
	}

	/**
	 * Get a list of the readings currently held.
	 * 
	 * @return a list of readings.
	 */
	public List<Reading> readings() {
		LOGGER.entering(CLASS_NAME, "readings");
		List<Reading> copyList = readings.stream().sorted().collect(Collectors.toList());
		LOGGER.exiting(CLASS_NAME, "readings", copyList);
		return copyList;
	}

	/**
	 * Find entries for a given date.
	 * 
	 * @param date a LocalDate to be used for locating entries.
	 * @return - an array of Readings.
	 */
	public Reading[] readingsFor(LocalDate date) {
		LOGGER.entering(CLASS_NAME, "readingsFor");
		List<Reading> list = readings.stream().filter((r) -> r.date().equals(date)).collect(Collectors.toList());
		Reading[] result = list.toArray(new Reading[] {});
		LOGGER.exiting(CLASS_NAME, "readingsFor", result);
		return result;
	}

	/**
	 * Find how many entries exist for a given date.
	 * 
	 * @param date a LocalDate to be used for locating entries.
	 * @return - a count of entries for that date..
	 */
	public long countEntries(LocalDate date) {
		LOGGER.entering(CLASS_NAME, "countEntries");
		long count = readings.stream().filter((r) -> r.date().equals(date)).count();
		LOGGER.exiting(CLASS_NAME, "countEntries", count);
		return count;
	}

	/**
	 * Find the file that stores the readings.
	 * 
	 * @return - the file used to store the readings.
	 */
	public File dataFile() {
		LOGGER.entering(CLASS_NAME, "");
		LOGGER.exiting(CLASS_NAME, "");
		return dataFile;
	}

	private void updateStorage() {
		LOGGER.entering(CLASS_NAME, "updateStorage");
		storage.storeData(readingsStore);
		LOGGER.exiting(CLASS_NAME, "updateStorage");
	}

	private void updateFailed(Exception e) {
		LOGGER.entering(CLASS_NAME, "updateFailed", e);
		readingsStore.signalStoreFailed(e);
		LOGGER.exiting(CLASS_NAME, "updateFailed");
	}

	private File obtainModelDirectory() {
		LOGGER.entering(CLASS_NAME, "");
		File rootDirectory = ApplicationConfiguration.rootDirectory();
		File applicationDirectory = new File(rootDirectory,
				ApplicationConfiguration.applicationDefinition().applicationName());
		File modelDirectory = new File(applicationDirectory, ModelConstants.MODEL);
		if (!modelDirectory.exists()) {
			LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does not exist");
			if (!modelDirectory.mkdirs()) {
				LOGGER.warning("Unable to create model directory");
				modelDirectory = null;
			} else {
				LOGGER.fine("Created model directory " + modelDirectory.getAbsolutePath());
			}
		} else {
			LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does exist");
		}
		LOGGER.exiting(CLASS_NAME, "", modelDirectory);
		return modelDirectory;
	}
}
