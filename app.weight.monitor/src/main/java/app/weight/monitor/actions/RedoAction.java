package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Create a redo action.
 */
public class RedoAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	/**
	 * Create the redo action for this application.
	 * 
	 * @param application - the application.
	 */
	public RedoAction(IApplication application) {
		super("redo");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.redoAction();
	}

}
