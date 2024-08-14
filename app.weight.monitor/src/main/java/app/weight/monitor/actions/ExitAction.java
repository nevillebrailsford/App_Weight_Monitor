package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Create a copy action.
 */
public class ExitAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	/**
	 * Create the copy action for this application.
	 * 
	 * @param application - the application.
	 */
	public ExitAction(IApplication application) {
		super("Exit");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.exitAction();
	}

}
