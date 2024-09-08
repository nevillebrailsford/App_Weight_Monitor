package app.weight.monitor.menu;

import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import app.weight.monitor.actions.WeightActionFactory;
import app.weight.monitor.application.IWeightApplication;
import application.definition.ApplicationConfiguration;
import application.menu.AbstractMenuBar;

/**
 * This class provides the menu bar for the weight monitor application.
 */
public class WeightMonitorMenuBar extends AbstractMenuBar {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitorMenuBar.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private JMenuItem copyItem = null;
	private JMenuItem pasteItem = null;
	private JMenuItem deleteItem = null;

	/**
	 * Create the menu bar using the application.
	 * 
	 * @param application - the application.
	 */
	public WeightMonitorMenuBar(IWeightApplication application) {
		super(WeightActionFactory.instance(application));
		LOGGER.entering(CLASS_NAME, "init");
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void addToEditMenu(JMenu editMenu) {
		LOGGER.entering(CLASS_NAME, "addToEditMenu");
		copyItem = new JMenuItem(WeightActionFactory.instance().copyAction());
		pasteItem = new JMenuItem(WeightActionFactory.instance().pasteAction());
		deleteItem = new JMenuItem(WeightActionFactory.instance().deleteAction());
		editMenu.addSeparator();
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(deleteItem);
		LOGGER.exiting(CLASS_NAME, "addToEditMenu");
	}

}
