package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Create a paste action.
 */
public class PasteAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The application.
	 */
	private IApplication application;

	/**
	 * Create the paste action for this application.
	 * 
	 * @param application - the application.
	 */
	public PasteAction(IApplication application) {
		super("Paste");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.pasteAction();
	}

}
