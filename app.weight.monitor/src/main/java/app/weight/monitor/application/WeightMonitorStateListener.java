package app.weight.monitor.application;

import application.change.ChangeManager;
import application.change.ChangeStateListener;

public class WeightMonitorStateListener implements ChangeStateListener {

	private IApplication application = null;

	public WeightMonitorStateListener(IApplication application) {
		this.application = application;
		ChangeManager.instance().addListener(this);
	}

	@Override
	public void stateChanged() {
		application.changeStateChange();
	}

}
