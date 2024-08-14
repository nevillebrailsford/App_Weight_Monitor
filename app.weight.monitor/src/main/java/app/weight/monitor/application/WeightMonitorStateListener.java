package app.weight.monitor.application;

import application.change.ChangeManager;
import application.change.ChangeStateListener;

/**
 * This class listens for state changes within the Change manager.
 */
public class WeightMonitorStateListener implements ChangeStateListener {

	private IApplication application = null;

	/**
	 * Create the listener using the application.
	 * 
	 * @param application - the current application.
	 */
	public WeightMonitorStateListener(IApplication application) {
		this.application = application;
		ChangeManager.instance().addListener(this);
	}

	@Override
	public void stateChanged() {
		application.changeStateChange();
	}

}
