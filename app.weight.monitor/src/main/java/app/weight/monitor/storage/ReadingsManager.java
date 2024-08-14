package app.weight.monitor.storage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import app.weight.monitor.model.Reading;
import application.definition.ApplicationConfiguration;
import application.storage.Storage;

/**
 * ReadingManager provides the interface to the backing storage for the weight
 * monitor application.
 */
public class ReadingsManager extends AbstractTableModel implements ListModel<String> {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = ReadingsManager.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	private static ReadingsManager instance = null;
	private static String[] COLUMNS = { "Date", "Weight" };
	private static final int DATE = 0;
	private static final int WEIGHT = 1;

	private Vector<ListDataListener> listDataListeners = new Vector<ListDataListener>();

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
			fireListChanged();
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
	 * Get the reading at a certain index.
	 * 
	 * @param index - the index into the history of readings.
	 * @return - the requested reading
	 */
	public Reading reading(int index) {
		LOGGER.entering(CLASS_NAME, "reading", index);
		Reading reading = readings.get(index);
		LOGGER.exiting(CLASS_NAME, "reading", reading);
		return reading;
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
		fireListChanged();
		fireTableDataChanged();
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

	// ListModel implementation

	@Override
	public int getSize() {
		return readings.size();
	}

	@Override
	public String getElementAt(int index) {
		Reading reading = readings.get(index);
		return reading.date().format(dateFormatter) + "       " + reading.weight();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.addElement(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.removeElement(l);
	}

	private void fireListChanged() {
		LOGGER.entering(CLASS_NAME, "fireListChanged");
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
		for (ListDataListener ldl : listDataListeners) {
			ldl.contentsChanged(e);
		}
		LOGGER.exiting(CLASS_NAME, "fireTreeStructureChanged");
	}

	// TableModel implementation

	@Override
	public int getRowCount() {
		return readings.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LOGGER.entering(CLASS_NAME, "getValueAt", new Object[] { rowIndex, columnIndex });
		Reading reading = readings.get(rowIndex);
		String result = "Unknown";
		switch (columnIndex) {
			case DATE:
				result = reading.date().format(dateFormatter);
				break;
			case WEIGHT:
				result = reading.weight();
				break;
		}
		LOGGER.exiting(CLASS_NAME, "getValueAt", result);
		return result;
	}

}
