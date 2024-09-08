package app.weight.monitor.actions;

import java.util.logging.Logger;

import javax.swing.JOptionPane;

import app.weight.monitor.application.IWeightApplication;
import application.action.BaseActionFactory;
import application.definition.ApplicationConfiguration;

/**
 * Factory for actions.
 */
public class WeightActionFactory extends BaseActionFactory {
	private static final String CLASS_NAME = WeightActionFactory.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();
	private CopyAction copyAction = null;
	private PasteAction pasteAction = null;
	private DeleteAction deleteAction = null;

	private static WeightActionFactory instance = null;

	/**
	 * Create a new instance of the action factory if not already created.
	 * 
	 * @param application - the application for this factory.
	 * @return - the action factory.
	 */
	public synchronized static WeightActionFactory instance(IWeightApplication... application) {
		LOGGER.entering(CLASS_NAME, "instance", application);
		if (instance == null) {
			if (application.length == 0) {
				JOptionPane.showMessageDialog(null, "Application was not specified on first call to instance.",
						"ActionFactory error.", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			instance = new WeightActionFactory();
			instance.application = application[0];

		}
		LOGGER.exiting(CLASS_NAME, "instance");
		return instance;
	}

	public WeightActionFactory() {
		super();
	}

	/**
	 * Obtain the copy action.
	 * <p>
	 * The action is created in the disabled state
	 * </p>
	 * 
	 * @return - a copy action.
	 */
	public CopyAction copyAction() {
		if (copyAction == null) {
			copyAction = new CopyAction((IWeightApplication) application);
			copyAction.setEnabled(false);
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
			deleteAction = new DeleteAction((IWeightApplication) application);
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
			pasteAction = new PasteAction((IWeightApplication) application);
			pasteAction.setEnabled(false);
		}
		return pasteAction;
	}

}
