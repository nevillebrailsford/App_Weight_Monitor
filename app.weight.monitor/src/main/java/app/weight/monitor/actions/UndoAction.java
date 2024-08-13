package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

/**
 * Create an undo action.
 */
public class UndoAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	/**
	 * Create the undo action for this application.
	 * 
	 * @param application - the application.
	 */
	public UndoAction(IApplication application) {
		super("Undo");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.undoAction();
	}

}
