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

	private ExitAction exitAction = null;
	private PreferencesAction preferencesAction = null;

	private HelpAboutAction helpAboutAction = null;

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
	 * <p>
	 * The action is created in the disabled state
	 * </p>
	 * 
	 * @return - a delete action.
	 */
	public DeleteAction deleteAction() {
		if (deleteAction == null) {
			deleteAction = new DeleteAction(application);
			deleteAction.setEnabled(false);
		}
		return deleteAction;
	}

	/**
	 * Obtain the paste action.
	 * <p>
	 * The action is created in the disabled state
	 * </p>
	 * 
	 * @return - a paste action.
	 */
	public PasteAction pasteAction() {
		if (pasteAction == null) {
			pasteAction = new PasteAction(application);
			pasteAction.setEnabled(false);
		}
		return pasteAction;
	}

	/**
	 * Obtain the redo action.
	 * <p>
	 * The action is created in the disabled state
	 * </p>
	 * 
	 * @return - a redo action.
	 */
	public RedoAction redoAction() {
		if (redoAction == null) {
			redoAction = new RedoAction(application);
			redoAction.setEnabled(false);
		}
		return redoAction;
	}

	/**
	 * Obtain the undo action.
	 * <p>
	 * The action is created in the disabled state
	 * </p>
	 * 
	 * @return - a undo action.
	 */
	public UndoAction undoAction() {
		if (undoAction == null) {
			undoAction = new UndoAction(application);
			undoAction.setEnabled(false);
		}
		return undoAction;
	}

	/**
	 * Obtain the exit action.
	 * 
	 * @return - an exit action.
	 */
	public ExitAction exitAction() {
		if (exitAction == null) {
			exitAction = new ExitAction(application);
		}
		return exitAction;
	}

	/**
	 * Obtain the preferences action.
	 * 
	 * @return - a preferences action.
	 */
	public PreferencesAction preferencesAction() {
		if (preferencesAction == null) {
			preferencesAction = new PreferencesAction(application);
		}
		return preferencesAction;
	}

	/**
	 * Obtain the help about action.
	 * 
	 * @return - a help about action.
	 */
	public HelpAboutAction helpAboutAction() {
		if (helpAboutAction == null) {
			helpAboutAction = new HelpAboutAction(application);
		}
		return helpAboutAction;
	}

}
