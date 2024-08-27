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

import app.weight.monitor.BarChartModel;
import app.weight.monitor.InformationModel;
import app.weight.monitor.model.Reading;
import application.definition.ApplicationConfiguration;
import application.storage.Storage;

/**
 * ReadingManager provides the interface to the backing storage for the weight
 * monitor application.
 */
/**
 * 
 */
public class ReadingsManager extends AbstractTableModel implements ListModel<String>, InformationModel, BarChartModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = ReadingsManager.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private static final DateTimeFormatter infoDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
		LOGGER.exiting(CLASS_NAME, "instance", instance);
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
		LOGGER.entering(CLASS_NAME, "initialise");
		synchronized (this.readings) {
			this.readings.addAll(readings);
			Collections.sort(this.readings);
		}
		LOGGER.exiting(CLASS_NAME, "initialise");
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
		LOGGER.entering(CLASS_NAME, "dataFile");
		LOGGER.exiting(CLASS_NAME, "dataFile");
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
		LOGGER.entering(CLASS_NAME, "obtainModelDirectory");
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
		LOGGER.exiting(CLASS_NAME, "obtainModelDirectory", modelDirectory);
		return modelDirectory;
	}

	// ListModel implementation

	@Override
	public int getSize() {
		LOGGER.entering(CLASS_NAME, "getSize");
		int result = readings.size();
		LOGGER.exiting(CLASS_NAME, "getSize", result);
		return result;
	}

	@Override
	public String getElementAt(int index) {
		LOGGER.entering(CLASS_NAME, "getElementAt");
		Reading reading = readings.get(index);
		String result = reading.date().format(infoDateFormatter) + "       " + reading.weight();
		LOGGER.exiting(CLASS_NAME, "getElementAt", result);
		return result;
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		LOGGER.entering(CLASS_NAME, "addListDataListener", l);
		listDataListeners.addElement(l);
		LOGGER.exiting(CLASS_NAME, "addListDataListener");
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		LOGGER.entering(CLASS_NAME, "removeListDataListener", l);
		listDataListeners.removeElement(l);
		LOGGER.exiting(CLASS_NAME, "removeListDataListener");
	}

	private void fireListChanged() {
		LOGGER.entering(CLASS_NAME, "fireListChanged");
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
		for (ListDataListener ldl : listDataListeners) {
			ldl.contentsChanged(e);
		}
		LOGGER.exiting(CLASS_NAME, "fireListChanged");
	}

	// TableModel implementation

	@Override
	public int getRowCount() {
		LOGGER.entering(CLASS_NAME, "getRowCount");
		int result = readings.size();
		LOGGER.exiting(CLASS_NAME, "getRowCount", result);
		return result;
	}

	@Override
	public int getColumnCount() {
		LOGGER.entering(CLASS_NAME, "getColumnCount");
		int result = COLUMNS.length;
		LOGGER.exiting(CLASS_NAME, "getColumnCount", result);
		return result;
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

	// InformationModel implementation

	@Override
	public int numberOfReadings() {
		LOGGER.entering(CLASS_NAME, "numberOfReadings");
		int result = readings.size();
		LOGGER.exiting(CLASS_NAME, "numberOfReadings", result);
		return result;
	}

	@Override
	public int numberOfWeeks() {
		LOGGER.entering(CLASS_NAME, "numberOfWeeks");
		long first = LocalDate.parse(earliestDate(), infoDateFormatter).toEpochDay();
		long last = LocalDate.parse(latestDate(), infoDateFormatter).toEpochDay();
		int days = (int) (last - first);
		int result = days / 7;
		LOGGER.exiting(CLASS_NAME, "numberOfWeeks", result);
		return result;
	}

	@Override
	public String earliestDate() {
		LOGGER.entering(CLASS_NAME, "earliestDate");
		Reading reading = readings.get(0);
		String result = reading.date().format(infoDateFormatter);
		LOGGER.exiting(CLASS_NAME, "earliestDate", result);
		return result;
	}

	@Override
	public String latestDate() {
		LOGGER.entering(CLASS_NAME, "latestDate");
		Reading reading = readings.get(readings.size() - 1);
		String result = reading.date().format(infoDateFormatter);
		LOGGER.exiting(CLASS_NAME, "latestDate", result);
		return result;
	}

	@Override
	public String earliestWeight() {
		LOGGER.entering(CLASS_NAME, "earliestWeight");
		Reading reading = readings.get(0);
		double weight = Double.parseDouble(reading.weight());
		String result = String.format("%.2f", weight);
		LOGGER.exiting(CLASS_NAME, "earliestWeight", result);
		return result;
	}

	@Override
	public String latestWeight() {
		LOGGER.entering(CLASS_NAME, "latestWeight");
		Reading reading = readings.get(readings.size() - 1);
		double weight = Double.parseDouble(reading.weight());
		String result = String.format("%.2f", weight);
		LOGGER.exiting(CLASS_NAME, "latestWeight", result);
		return result;
	}

	@Override
	public String changeInWeight() {
		LOGGER.entering(CLASS_NAME, "changeInWeight");
		double firstWeight = Double.parseDouble(earliestWeight());
		double lastWeight = Double.parseDouble(latestWeight());
		String result = String.format("%.2f", firstWeight - lastWeight);
		LOGGER.exiting(CLASS_NAME, "changeInWeight", result);
		return result;
	}

	@Override
	public String lightestWeight() {
		LOGGER.entering(CLASS_NAME, "lightestWeight");
		double min = readings.stream().map((r) -> Double.parseDouble(r.weight())).min(Double::compare).get();
		String result = String.format("%.2f", min);
		LOGGER.exiting(CLASS_NAME, "lightestWeight", result);
		return result;
	}

	@Override
	public String heaviestWeight() {
		LOGGER.entering(CLASS_NAME, "heaviestWeight");
		double max = readings.stream().map((r) -> Double.parseDouble(r.weight())).max(Double::compare).get();
		String result = String.format("%.2f", max);
		LOGGER.exiting(CLASS_NAME, "heaviestWeight", result);
		return result;
	}

	@Override
	public String averageWeight() {
		LOGGER.entering(CLASS_NAME, "averageWeight");
		double sum = readings.stream().mapToDouble((r) -> Double.parseDouble(r.weight())).sum();
		double avg = sum / numberOfReadings();
		String result = String.format("%.2f", avg);
		LOGGER.exiting(CLASS_NAME, "averageWeight", result);
		return result;
	}

	@Override
	public String averageChangePerWeek() {
		LOGGER.entering(CLASS_NAME, "averageChangePerWeek");
		double changeInWeight = Double.parseDouble(changeInWeight());
		int numberOfWeeks = numberOfWeeks();
		double avg = changeInWeight / numberOfWeeks;
		String result = String.format("%.2f", avg);
		LOGGER.exiting(CLASS_NAME, "averageChangePerWeek", result);
		return result;
	}

	// BarChartModel implementation

	@Override
	public int numberOfColumns() {
		LOGGER.entering(CLASS_NAME, "numberOfColumns");
		double lWeight = Double.parseDouble(lightestWeight());
		double hWeight = Double.parseDouble(heaviestWeight());
		double diffWeight = Math.ceil(hWeight) - Math.floor(lWeight);
		int numberOfColumns = (int) diffWeight * 2;
		LOGGER.exiting(CLASS_NAME, "numberOfColumns", numberOfColumns);
		return numberOfColumns;
	}

	@Override
	public double valueAtColumn(int columnNumber) {
		LOGGER.entering(CLASS_NAME, "valueAtColumn", columnNumber);
		double value = Double.parseDouble(lightestWeight());
		for (int i = 0; i < columnNumber; i++) {
			value += 0.5;
		}
		LOGGER.exiting(CLASS_NAME, "valueAtColumn", value);
		return value;
	}

	@Override
	public int numberForValue(double value) {
		LOGGER.entering(CLASS_NAME, "numberForValue", value);
		int count = (int) readings.stream().filter((r) -> r.weight().equals(Double.toString(value))).count();
		LOGGER.exiting(CLASS_NAME, "numberForValue", count);
		return count;
	}

}
