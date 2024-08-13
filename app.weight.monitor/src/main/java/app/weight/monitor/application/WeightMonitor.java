package app.weight.monitor.application;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import app.weight.monitor.Constants;
import app.weight.monitor.application.chart.WeightGraph;
import app.weight.monitor.application.gui.EditorPanel;
import app.weight.monitor.storage.ReadingsLoad;
import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.storage.StoreDetails;

/**
 * The application to record and monitor my weight readings.
 */
public class WeightMonitor extends ApplicationBaseForGUI implements IApplication {

	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitor.class.getName();

	private static Logger LOGGER = null;

	private JFrame parent;

	GridBagConstraints gbc;

	JTabbedPane weightTabbedPane = new JTabbedPane();
	JPanel editorPanel = null;
	WeightGraph plotPanel = new WeightGraph();

	@Override
	public void configureStoreDetails() {
		dataLoader = new ReadingsLoad();
		storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.READINGS_FILE);
	}

	@Override
	public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
		ApplicationDefinition definition = new WeightMonitorDefinition(parameters.getNamed().get("name"));
		return definition;
	}

	@Override
	public void start(JFrame parent) {
		LOGGER = ApplicationConfiguration.logger();
		LOGGER.entering(CLASS_NAME, "start");
		this.parent = parent;
		System.out.println(
				"Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is starting");
		this.parent.setLayout(new GridBagLayout());
		editorPanel = new EditorPanel(this);
		configureComponents();
		layoutComponents();
		loadData();
		pack();
		editorPanel.requestFocus();
		LOGGER.exiting(CLASS_NAME, "start");

	}

	@Override
	public void terminate() {
		LOGGER.entering(CLASS_NAME, "terminate");
		System.out.println(
				"Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is stopping");
		LOGGER.exiting(CLASS_NAME, "terminate");
	}

	/**
	 * Main entry point for the application.
	 * <p>
	 * The parameters that the application will read are:
	 * <li>--name=x where x is the name to be used by the application.</li>
	 * <li>--dir=x where x is the path to the working directory.</li>
	 * 
	 * @param args - any number of arguments passed in from command line. The values
	 *             must include the values for --name and --dir.
	 */
	public static void main(String[] args) {
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		launch(args);
	}

	private void configureComponents() {
		weightTabbedPane.setPreferredSize(new Dimension(500, 400));
		weightTabbedPane.addTab("Weight Editor", editorPanel);
		weightTabbedPane.addTab("Weight Plot", plotPanel);

	}

	private void layoutComponents() {
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		parent.add(weightTabbedPane, gbc);

	}

	private void loadData() {
		initializeData();
	}

	private void initializeData() {
		weightTabbedPane.setSelectedIndex(0);
	}

	// IApplication implementation

	@Override
	public void redoAction() {
		System.out.println("redo");
	}

	@Override
	public void undoAction() {
		System.out.println("undo");
	}

	@Override
	public void copyAction() {
		System.out.println("copy");
	}

	@Override
	public void pasteAction() {
		System.out.println("paste");
	}

	@Override
	public void deleteAction() {
		System.out.println("delete");
	}
}
