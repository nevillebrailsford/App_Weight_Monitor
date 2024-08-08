package app.weight.monitor.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import app.weight.monitor.model.Reading;
import application.audit.AuditService;
import application.definition.ApplicationConfiguration;
import application.storage.AbstractLoadData;

public class ReadingsLoad extends AbstractLoadData {
	private static final String CLASS_NAME = ReadingsLoad.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	@Override
	public void readData() throws IOException {
		LOGGER.entering(CLASS_NAME, "readData");
		try (BufferedReader archive = new BufferedReader(new FileReader(fileName()))) {
			AuditService.suspend();
			readDataFrom(archive);
			AuditService.resume();
		} catch (Exception e) {
			IOException exc = new IOException("PropertyStore: Exception occurred - " + e.getMessage(), e);
			LOGGER.throwing(CLASS_NAME, "readData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "readData");
		}
	}

	private void readDataFrom(BufferedReader archive) throws IOException {
		LOGGER.entering(CLASS_NAME, "readDataFrom");
		List<Reading> readings = new ArrayList<>();
		do {
			String reading = archive.readLine();
			StringTokenizer st = new StringTokenizer(reading, " ");
			if (st.countTokens() == 2) {
				LocalDate date = LocalDate.parse(st.nextToken(), dateFormatter);
				String weight = st.nextToken();
				Reading newReading = new Reading(date, weight);
				readings.add(newReading);
			}
		} while (archive.ready());
		ReadingsManager.instance().initialise(readings);
		LOGGER.exiting(CLASS_NAME, "readDataFrom");
	}
}
