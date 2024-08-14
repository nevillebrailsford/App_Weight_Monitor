package app.weight.monitor.change;

import java.util.logging.Logger;

import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsManager;
import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;

/**
 * A change request that will add a reading to the history of readings.
 */
public class AddReadingChange extends AbstractChange {
	private static final String CLASS_NAME = AddReadingChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Reading reading;

	/**
	 * Create the change request.
	 * 
	 * @param reading - the reading to be added.
	 */
	public AddReadingChange(Reading reading) {
		this.reading = reading;
	}

	@Override
	protected void doHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "doHook");
		redoHook();
		LOGGER.exiting(CLASS_NAME, "doHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		ReadingsManager.instance().addReading(reading);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		ReadingsManager.instance().deleteReading(reading);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}
}
