package app.weight.monitor.application;

import application.base.app.IApplication;

/**
 * A number of actions that the application will perform.
 */
public interface IWeightApplication extends IApplication {

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
	 * Change manager state has been updated.
	 */
	void changeStateChange();

	/**
	 * Copy manager state has changed.
	 */
	void copyStateChange();

}
