package app.weight.monitor.application;

import java.util.logging.Logger;

import application.definition.ApplicationConfiguration;
import application.replicate.CopyAndPaste;
import application.replicate.CopyAndPasteListener;

/**
 * This class listens for state changes in the copy and paste manager.
 */
public class WeightMonitorCopyListener implements CopyAndPasteListener {
	private static final String CLASS_NAME = WeightMonitorCopyListener.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private IApplication application = null;

	/**
	 * Create the listener, using the current application.
	 * 
	 * @param application - the current application.
	 */
	public WeightMonitorCopyListener(IApplication application) {
		LOGGER.entering(CLASS_NAME, "cinit");
		this.application = application;
		CopyAndPaste.instance().addListener(this);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	@Override
	public void copyChanged() {
		LOGGER.entering(CLASS_NAME, "copyChanged");
		application.copyStateChange();
		LOGGER.exiting(CLASS_NAME, "copyChanged");
	}

}
