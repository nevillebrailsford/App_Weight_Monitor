package app.weight.monitor.application;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import app.weight.monitor.Constants;
import app.weight.monitor.actions.WeightActionFactory;
import app.weight.monitor.application.chart.BarChartComponent;
import app.weight.monitor.application.chart.BarChartPainter;
import app.weight.monitor.application.chart.LineChartComponent;
import app.weight.monitor.application.chart.LineChartPainter;
import app.weight.monitor.application.gui.ChartPanel;
import app.weight.monitor.application.gui.EditorPanel;
import app.weight.monitor.application.gui.GUIConstants;
import app.weight.monitor.application.gui.InformationPanel;
import app.weight.monitor.menu.WeightMonitorMenuBar;
import app.weight.monitor.preferences.ColorChoice;
import app.weight.monitor.preferences.WeightMonitorPreferencesDialog;
import app.weight.monitor.storage.ReadingsLoad;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.replicate.CopyAndPaste;
import application.storage.StoreDetails;

/**
 * The application to record and monitor my weight readings.
 */
public class WeightMonitorApplication extends ApplicationBaseForGUI implements IWeightApplication {

	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitorApplication.class.getName();
	public static ColorChoice colorChoice = null;

	private static Logger LOGGER = null;

	private JFrame parent;

	GridBagConstraints gbc;

	JTabbedPane weightTabbedPane = new JTabbedPane();
	EditorPanel editorPanel = null;
	InformationPanel informationPanel = null;
	ChartPanel lineChartPanel = null;
	ChartPanel barChartPanel = null;

	@Override
	public StoreDetails configureStoreDetails() {
		dataLoader = new ReadingsLoad();
		StoreDetails storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.READINGS_FILE);
		return storeDetails;
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
		informationPanel = new InformationPanel();
		processPreferences();
		configureComponents();
		layoutComponents();
		loadData();
		new WeightMonitorCopyListener(this);
		LineChartPainter chartPainter = new LineChartPainter();
		LineChartComponent chartComponent = new LineChartComponent(ReadingsManager.instance(), chartPainter);
		lineChartPanel = new ChartPanel(chartComponent);
		weightTabbedPane.addTab("Weight Plot", lineChartPanel);
		BarChartPainter barChartPainter = new BarChartPainter(ReadingsManager.instance());
		BarChartComponent barChartComponent = new BarChartComponent(ReadingsManager.instance(), barChartPainter);
		barChartPanel = new ChartPanel(barChartComponent);
		weightTabbedPane.addTab("Weight Distribution", barChartPanel);
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
		weightTabbedPane.addTab("Information", informationPanel);
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
		WeightActionFactory.instance(this).undoAction().setEnabled(ChangeManager.instance().undoable());
		WeightActionFactory.instance(this).redoAction().setEnabled(ChangeManager.instance().redoable());
		LOGGER.exiting(CLASS_NAME, "setDoableActions");
	}

	private void setCopyableActions() {
		LOGGER.entering(CLASS_NAME, "setCopyableActions");
		WeightActionFactory.instance(this).pasteAction().setEnabled(CopyAndPaste.instance().paste() != null);
		LOGGER.exiting(CLASS_NAME, "setCopyableActions");
	}

	private static void processPreferences() {
		String background = IniFile.value(GUIConstants.BACKGROUND_COLOR);
		String chartLine = IniFile.value(GUIConstants.CHART_LINE_COLOR);
		String trendLine = IniFile.value(GUIConstants.TREND_LINE_COLOR);
		if (background == null || background.isEmpty() || background.equals("default")) {
			background = GUIConstants.DEFAULT_BACKGROUND_COLOR;
		}
		if (chartLine == null || chartLine.isEmpty() || chartLine.equals("default")) {
			chartLine = GUIConstants.DEFAULT_CHART_LINE_COLOR;
		}
		if (trendLine == null || trendLine.isEmpty() || trendLine.equals("default")) {
			trendLine = GUIConstants.DEFAULT_TREND_LINE_COLOR;
		}
		colorChoice = new ColorChoice(background, chartLine, trendLine);
	}

	// IWeightApplication implementation

	@Override
	public void copyAction() {
		LOGGER.entering(CLASS_NAME, "copyAction");
		editorPanel.copyAction();
		LOGGER.exiting(CLASS_NAME, "copyAction");
	}

	@Override
	public void pasteAction() {
		LOGGER.entering(CLASS_NAME, "pasteAction");
		editorPanel.pasteAction();
		LOGGER.exiting(CLASS_NAME, "pasteAction");
	}

	@Override
	public void deleteAction() {
		LOGGER.entering(CLASS_NAME, "deleteAction");
		editorPanel.deleteAction();
		LOGGER.exiting(CLASS_NAME, "deleteAction");
	}

	@Override
	public void preferencesAction() {
		LOGGER.entering(CLASS_NAME, "preferencesAction");
		WeightMonitorPreferencesDialog dialog = new WeightMonitorPreferencesDialog(parent);
		dialog.setVisible(true);
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "preferencesAction");
	}

	@Override
	public void copyStateChange() {
		setCopyableActions();
	}

}
