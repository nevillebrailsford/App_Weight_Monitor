package app.weight.monitor.application;

import application.replicate.CopyAndPaste;
import application.replicate.CopyAndPasteListener;

public class WeightMonitorCopyListener implements CopyAndPasteListener {

	private IApplication application = null;

	public WeightMonitorCopyListener(IApplication application) {
		this.application = application;
		CopyAndPaste.instance().addListener(this);
	}

	@Override
	public void copyChanged() {
		application.copyStateChange();
	}

}
