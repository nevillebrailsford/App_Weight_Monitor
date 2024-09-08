package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IWeightApplication;

/**
 * Create a paste action.
 */
public class PasteAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The application.
	 */
	private IWeightApplication application;

	/**
	 * Create the paste action for this application.
	 * 
	 * @param application - the application.
	 */
	public PasteAction(IWeightApplication application) {
		super("Paste");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.pasteAction();
	}

}
