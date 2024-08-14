package app.weight.monitor.application;

/**
 * A number of actions that the application will perform.
 */
public interface IApplication {

	/**
	 * Perform a copy action.
	 */
	void copyAction();

	/**
	 * Perform a delete action.
	 */
	void deleteAction();

	/**
	 * Perform a paste action.
	 */
	void pasteAction();

	/**
	 * Perform a redo action.
	 */
	void redoAction();

	/**
	 * Perform an undo action.
	 */
	void undoAction();

	/**
	 * Change manager state has been updated.
	 */
	void changeStateChange();

	/**
	 * Copy manager state has changed.
	 */
	void copyStateChange();

}
