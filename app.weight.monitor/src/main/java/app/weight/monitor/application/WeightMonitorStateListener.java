package app.weight.monitor.application;

import java.util.logging.Logger;

import application.change.ChangeManager;
import application.change.ChangeStateListener;
import application.definition.ApplicationConfiguration;

/**
 * This class listens for state changes within the Change manager.
 */
public class WeightMonitorStateListener implements ChangeStateListener {
	private static final String CLASS_NAME = WeightMonitorStateListener.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private IWeightApplication application = null;

	/**
	 * Create the listener using the application.
	 * 
	 * @param application - the current application.
	 */
	public WeightMonitorStateListener(IWeightApplication application) {
		LOGGER.entering(CLASS_NAME, "cinit");
		this.application = application;
		ChangeManager.instance().addListener(this);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	@Override
	public void stateChanged() {
		LOGGER.entering(CLASS_NAME, "stateChanged");
		application.changeStateChange();
		LOGGER.exiting(CLASS_NAME, "stateChanged");
	}

}
