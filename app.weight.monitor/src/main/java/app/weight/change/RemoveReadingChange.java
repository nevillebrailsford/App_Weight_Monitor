package app.weight.change;

import java.util.logging.Logger;

import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsManager;
import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;

/**
 * A change request that will remove a reading from the history of readings.
 */
public class RemoveReadingChange extends AbstractChange {
	private static final String CLASS_NAME = RemoveReadingChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Reading reading;

	/**
	 * Create the change request.
	 * 
	 * @param reading - the reading to be removed.
	 */
	public RemoveReadingChange(Reading reading) {
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
		ReadingsManager.instance().deleteReading(reading);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		ReadingsManager.instance().addReading(reading);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}
}
