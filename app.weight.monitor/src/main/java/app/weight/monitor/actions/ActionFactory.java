package app.weight.monitor.actions;

import app.weight.monitor.application.IApplication;

/**
 * Factory for actions.
 */
public class ActionFactory {
	private UndoAction undoAction = null;
	private RedoAction redoAction = null;
	private CopyAction copyAction = null;
	private PasteAction pasteAction = null;
	private DeleteAction deleteAction = null;

	private static ActionFactory instance = null;
	private IApplication application;

	/**
	 * Create a new instance of the action factory if not already created.
	 * 
	 * @param application - the application for this factory.
	 * @return - the action factory.
	 */
	public synchronized static ActionFactory instance(IApplication application) {
		if (instance == null) {
			instance = new ActionFactory();
			instance.application = application;
		}
		return instance;
	}

	private ActionFactory() {
	}

	/**
	 * Obtain the copy action.
	 * 
	 * @return - a copy action.
	 */
	public CopyAction copyAction() {
		if (copyAction == null) {
			copyAction = new CopyAction(application);
		}
		return copyAction;
	}

	/**
	 * Obtain the delete action.
	 * 
	 * @return - a delete action.
	 */
	public DeleteAction deleteAction() {
		if (deleteAction == null) {
			deleteAction = new DeleteAction(application);
		}
		return deleteAction;
	}

	/**
	 * Obtain the paste action.
	 * 
	 * @return - a paste action.
	 */
	public PasteAction pasteAction() {
		if (pasteAction == null) {
			pasteAction = new PasteAction(application);
		}
		return pasteAction;
	}

	/**
	 * Obtain the redo action.
	 * 
	 * @return - a redo action.
	 */
	public RedoAction redoAction() {
		if (redoAction == null) {
			redoAction = new RedoAction(application);
		}
		return redoAction;
	}

	/**
	 * Obtain the undo action.
	 * 
	 * @return - a undo action.
	 */
	public UndoAction undoAction() {
		if (undoAction == null) {
			undoAction = new UndoAction(application);
		}
		return undoAction;
	}
}
