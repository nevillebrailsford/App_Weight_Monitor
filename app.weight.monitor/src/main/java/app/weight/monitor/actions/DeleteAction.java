package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Create a delete action.
 */
public class DeleteAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The application.
	 */
	private IApplication application;

	/**
	 * Create the delete action for this application.
	 * 
	 * @param application - the application.
	 */
	public DeleteAction(IApplication application) {
		super("Delete");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.deleteAction();
	}

}
