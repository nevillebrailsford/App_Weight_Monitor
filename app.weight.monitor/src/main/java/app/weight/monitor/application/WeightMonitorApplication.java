package app.weight.monitor.application;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import app.weight.monitor.Constants;
import app.weight.monitor.actions.ActionFactory;
import app.weight.monitor.application.chart.WeightGraph;
import app.weight.monitor.application.gui.EditorPanel;
import app.weight.monitor.application.gui.WeightMonitorMenuBar;
import app.weight.monitor.storage.ReadingsLoad;
import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.PreferencesDialog;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.replicate.CopyAndPaste;
import application.storage.StoreDetails;

/**
 * The application to record and monitor my weight readings.
 */
public class WeightMonitorApplication extends ApplicationBaseForGUI implements IApplication {

	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitorApplication.class.getName();

	private static Logger LOGGER = null;

	private JFrame parent;

	GridBagConstraints gbc;

	JTabbedPane weightTabbedPane = new JTabbedPane();
	EditorPanel editorPanel = null;
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
		this.parent.setJMenuBar(new WeightMonitorMenuBar(this));
		this.parent.setLayout(new GridBagLayout());
		editorPanel = new EditorPanel(this);
		configureComponents();
		layoutComponents();
		loadData();
		new WeightMonitorStateListener(this);
		new WeightMonitorCopyListener(this);
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
	 * <p>
	 * --name=x where x is the name to be used by the application.
	 * <p>
	 * --dir=x where x is the path to the working directory.
	 * 
	 * @param args - any number of arguments passed in from command line. The values
	 *             must include the values for --name and --dir.
	 */
	public static void main(String[] args) {
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		launch(args);
	}

	private void configureComponents() {
		LOGGER.entering(CLASS_NAME, "configureComponents");
		weightTabbedPane.setPreferredSize(new Dimension(500, 400));
		weightTabbedPane.addTab("Weight Editor", editorPanel);
		weightTabbedPane.addTab("Weight Plot", plotPanel);
		LOGGER.exiting(CLASS_NAME, "configureComponents");
	}

	private void layoutComponents() {
		LOGGER.entering(CLASS_NAME, "layoutComponents");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		parent.add(weightTabbedPane, gbc);
		LOGGER.exiting(CLASS_NAME, "layoutComponents");
	}

	private void loadData() {
		LOGGER.entering(CLASS_NAME, "loadData");
		initializeData();
		LOGGER.exiting(CLASS_NAME, "loadData");
	}

	private void initializeData() {
		LOGGER.entering(CLASS_NAME, "initializeData");
		weightTabbedPane.setSelectedIndex(0);
		setDoableActions();
		LOGGER.exiting(CLASS_NAME, "initializeData");
	}

	private void setDoableActions() {
		LOGGER.entering(CLASS_NAME, "setDoableActions");
		ActionFactory.instance(this).undoAction().setEnabled(ChangeManager.instance().undoable());
		ActionFactory.instance(this).redoAction().setEnabled(ChangeManager.instance().redoable());
		LOGGER.exiting(CLASS_NAME, "setDoableActions");
	}

	private void setCopyableActions() {
		LOGGER.entering(CLASS_NAME, "setCopyableActions");
		ActionFactory.instance(this).pasteAction().setEnabled(CopyAndPaste.instance().paste() != null);
		LOGGER.exiting(CLASS_NAME, "setCopyableActions");
	}

	private String getBuildInformation(String applicationName) {
		LOGGER.entering(applicationName, "getBuildInformation");
		String result = "";
		StringBuilder builder = new StringBuilder(applicationName);
		try {
			builder.append("\nBuild: ").append(ApplicationDefinition.getFromManifest("Build-Number", getClass())
					.orElse("Could not be determined"));
			builder.append("\nBuild Date: ").append(
					ApplicationDefinition.getFromManifest("Build-Date", getClass()).orElse("Could not be determined"));
		} catch (Exception e) {
			builder.append("\nUnable to gather build version and date information\ndue to exception " + e.getMessage());
			LOGGER.fine("Caught exception: " + e.getMessage());
		}
		result = builder.toString();
		LOGGER.exiting(applicationName, "getBuildInformation", result);
		return result;
	}

	// IApplication implementation

	@Override
	public void redoAction() {
		editorPanel.redoAction();
	}

	@Override
	public void undoAction() {
		editorPanel.undoAction();
	}

	@Override
	public void copyAction() {
		editorPanel.copyAction();
	}

	@Override
	public void pasteAction() {
		editorPanel.pasteAction();
	}

	@Override
	public void deleteAction() {
		editorPanel.deleteAction();
	}

	@Override
	public void changeStateChange() {
		setDoableActions();
	}

	@Override
	public void copyStateChange() {
		setCopyableActions();
	}

	@Override
	public void exitAction() {
		shutdown();
	}

	@Override
	public void preferencesAction() {
		PreferencesDialog dialog = new PreferencesDialog(parent);
		dialog.setVisible(true);
		dialog.dispose();

	}

	@Override
	public void helpAboutAction() {
		String applicationName = ApplicationConfiguration.applicationDefinition().applicationName();
		String title = "About " + applicationName;
		String message = getBuildInformation(applicationName);
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

}
