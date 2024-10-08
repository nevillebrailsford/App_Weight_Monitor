package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IWeightApplication;

/**
 * Create a copy action.
 */
public class CopyAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The application.
	 */
	private IWeightApplication application;

	/**
	 * Create the copy action for this application.
	 * 
	 * @param application - the application.
	 */
	public CopyAction(IWeightApplication application) {
		super("Copy");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.copyAction();
	}

}
