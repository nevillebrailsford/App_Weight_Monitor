package app.weight.monitor.application;

import application.replicate.CopyAndPaste;
import application.replicate.CopyAndPasteListener;

/**
 * This class listens for state changes in the copy and paste manager.
 */
public class WeightMonitorCopyListener implements CopyAndPasteListener {

	private IApplication application = null;

	/**
	 * Create the listener, using the current application.
	 * 
	 * @param application - the current application.
	 */
	public WeightMonitorCopyListener(IApplication application) {
		this.application = application;
		CopyAndPaste.instance().addListener(this);
	}

	@Override
	public void copyChanged() {
		application.copyStateChange();
	}

}
