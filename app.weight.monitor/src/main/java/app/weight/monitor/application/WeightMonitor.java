package app.weight.monitor.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

import com.toedter.calendar.JCalendar;

import app.weight.monitor.Constants;
import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsLoad;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.ColoredPanel;
import application.base.app.gui.GUIConstants;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.storage.StoreDetails;

/**
 * The application to record and monitor my weight readings.
 */
public class WeightMonitor extends ApplicationBaseForGUI {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitor.class.getName();

	private static Logger LOGGER = null;

	private JFrame parent;

	GridBagConstraints gbc;

	JTabbedPane weightTabbedPane = new JTabbedPane();
	JPanel editorPanel = new ColoredPanel();
	WeightPlotPanel plotPanel = new WeightPlotPanel();

	JLabel fileLabel = new JLabel();
	JTextArea fileTextArea = new JTextArea();
	JCalendar weightCalendar = new JCalendar();
	JLabel weightLabel = new JLabel();
	JTextField weightTextField = new JTextField();
	JButton addButton = new JButton();

	JLabel weightsListLabel = new JLabel();
	JScrollPane weightsScrollPane = new JScrollPane();
	JList<String> weightsList = new JList<>();
	static DefaultListModel<String> weightsListModel = new DefaultListModel<>();
	JButton deleteButton = new JButton();

	@Override
	public void configureStoreDetails() {
		dataLoader = new ReadingsLoad();
		storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.READINGS_FILE);
	}

	@Override
	public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
		ApplicationDefinition definition = new ApplicationDefinition(parameters.getNamed().get("name")) {

			@Override
			public boolean hasModelFile() {
				return true;
			}

			@Override
			public Optional<Color> bottomColor() {
				String bottom = IniFile.value(GUIConstants.BOTTOM_COLOR);
				if (bottom.isEmpty() || bottom.equals("default")) {
					bottom = "lightsteelblue";
					IniFile.store(GUIConstants.BOTTOM_COLOR, bottom);
				}
				Color bottomColor = ColorProvider.get(bottom);
				return Optional.ofNullable(bottomColor);
			}

			@Override
			public Optional<Color> topColor() {
				String top = IniFile.value(GUIConstants.TOP_COLOR);
				if (top.isEmpty() || top.equals("default")) {
					top = "lightcyan";
					IniFile.store(GUIConstants.TOP_COLOR, top);
				}
				Color topColor = ColorProvider.get(top);
				return Optional.ofNullable(topColor);
			}
		};
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
		configureComponents();
		layoutComponents();
		loadData();
		pack();
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

		editorPanel.setBackground(new Color(192, 192, 255));
		editorPanel.setLayout(new GridBagLayout());

		plotPanel.setBackground(new Color(255, 192, 192));

		fileLabel.setText("Current Weight File");
		fileLabel.setFont(new Font("Arial", Font.BOLD, 14));
		fileTextArea.setPreferredSize(new Dimension(220, 50));
		fileTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
		fileTextArea.setEditable(false);
		fileTextArea.setBackground(Color.WHITE);
		fileTextArea.setLineWrap(true);
		fileTextArea.setWrapStyleWord(true);

		weightCalendar.setPreferredSize(new Dimension(220, 200));
		weightCalendar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		weightCalendar.addPropertyChangeListener((event) -> {
			weightCalendarPropertyChange(event);
		});

		weightLabel.setText("Weight(kg)");
		weightLabel.setFont(new Font("Arial", Font.BOLD, 14));
		weightTextField.setPreferredSize(new Dimension(100, 25));
		weightTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		weightTextField.addActionListener((event) -> weightTextFieldActionPerformed(event));
		weightTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				addButton.setEnabled(validateWeight());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				addButton.setEnabled(validateWeight());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				addButton.setEnabled(validateWeight());
			}
		});

		addButton.setText("Add Weight to File");
		addButton.addActionListener((event) -> addButtonActionPerformed(event));
		addButton.setEnabled(false);

		weightsListLabel.setText("Date         Weight(kg)");
		weightsListLabel.setFont(new Font("Courier New", Font.BOLD, 16));

		weightsScrollPane.setPreferredSize(new Dimension(250, 300));
		weightsList.setFont(new Font("Courier New", Font.PLAIN, 16));
		weightsScrollPane.setViewportView(weightsList);
		weightsList.addListSelectionListener((event) -> {
			weightsListValueChanged(event);
		});

		deleteButton.setText("Delete Selection");
		deleteButton.addActionListener((event) -> deleteButtonActionPerformed(event));
		deleteButton.setEnabled(false);
	}

	private void deleteButtonActionPerformed(ActionEvent event) {
		System.out.println("deleteButton");
	}

	protected void weightsListValueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			if (weightsList.getSelectedIndex() == -1) {
				deleteButton.setEnabled(false);
			} else {
				deleteButton.setEnabled(true);
			}
		}
	}

	private void addButtonActionPerformed(ActionEvent event) {
		System.out.println("addButton");
	}

	private void weightTextFieldActionPerformed(ActionEvent event) {
		addButton.doClick();
	}

	protected void weightCalendarPropertyChange(PropertyChangeEvent event) {
		if (event.getNewValue() != null) {
			if (event.getNewValue() instanceof Calendar) {
				Calendar cal = (Calendar) event.getNewValue();
				LocalDate.ofInstant(cal.toInstant(), ZoneId.systemDefault());
			}
		}
	}

	private void layoutComponents() {
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		parent.add(weightTabbedPane, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 10, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		editorPanel.add(fileLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 10, 10, 0);
		editorPanel.add(fileTextArea, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 10, 0, 5);
		editorPanel.add(weightCalendar, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(10, 10, 0, 0);
		editorPanel.add(weightLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(10, 5, 0, 0);
		editorPanel.add(weightTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 0, 0);
		editorPanel.add(addButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 0, 0);
		editorPanel.add(weightsListLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridheight = 3;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		editorPanel.add(weightsScrollPane, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		editorPanel.add(deleteButton, gbc);
	}

	private void loadData() {
		initializeData();
		fileTextArea.setText(ReadingsManager.instance().dataFile().getAbsolutePath());
		for (Reading reading : ReadingsManager.instance().readings()) {
			weightsListModel.addElement(reading.toString());
		}
		weightsList.ensureIndexIsVisible(ReadingsManager.instance().readings().size() - 1);
	}

	private void initializeData() {
		weightTabbedPane.setSelectedIndex(0);
		weightCalendar.setDate(new Date());
		weightsListModel.clear();
		weightsList.setModel(weightsListModel);
		fileTextArea.setText("New File");
		weightTextField.setText("");
		weightTextField.requestFocus();
	}

	private boolean validateWeight() {
		String match = "^\\d*\\.?\\d+|\\d+\\.\\d*$";
		String sel = weightTextField.getText();
		if (sel == null || sel.isEmpty()) {
			return false;
		}
		return sel.matches(match);
	}

	class WeightPlotPanel extends JPanel {

	}
}
