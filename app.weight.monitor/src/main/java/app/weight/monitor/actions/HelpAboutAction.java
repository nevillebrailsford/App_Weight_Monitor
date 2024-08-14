package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Class to display information about the application.
 */
public class HelpAboutAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The application.
	 */
	private IApplication application;

	/**
	 * Create the action using the application.
	 * 
	 * @param application - the application.
	 */
	public HelpAboutAction(IApplication application) {
		super("About");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.helpAboutAction();
	}

}
