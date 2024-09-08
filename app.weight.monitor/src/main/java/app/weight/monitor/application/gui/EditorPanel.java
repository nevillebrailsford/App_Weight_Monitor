package app.weight.monitor.application.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;

import com.toedter.calendar.JCalendar;

import app.weight.monitor.actions.WeightActionFactory;
import app.weight.monitor.application.IWeightApplication;
import app.weight.monitor.change.AddReadingChange;
import app.weight.monitor.change.RemoveReadingChange;
import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.gui.ColoredPanel;
import application.change.Change;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.replicate.CopyAndPaste;
import application.thread.ThreadServices;

/**
 * Main panel for entering data.
 */
public class EditorPanel extends ColoredPanel implements ListDataListener {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = EditorPanel.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private LocalDate selectedDate = LocalDate.now();

	private GridBagConstraints gbc;

	private JLabel fileLabel = new JLabel();
	private JTextArea fileTextArea = new JTextArea();
	private JCalendar weightCalendar = new JCalendar();
	private JLabel weightLabel = new JLabel();
	private JTextField weightTextField = new JTextField();
	private JButton addButton = new JButton();

	private JLabel weightsListLabel = new JLabel();
	private JScrollPane weightsScrollPane = new JScrollPane();
	private JList<String> weightsList = new JList<>();
	private ListModel<String> weightsListModel = null;
	private JButton deleteButton = new JButton();

	private WeightActionFactory actionFactory = null;

	/**
	 * Create the editor panel for this application.
	 * 
	 * @param application - the application that this panel is used by.
	 */
	public EditorPanel(IWeightApplication application) {
		super();
		LOGGER.entering(CLASS_NAME, "init");
		actionFactory = WeightActionFactory.instance();
		weightsListModel = ReadingsManager.instance();
		ReadingsManager.instance().addListDataListener(this);
		setLayout(new GridBagLayout());
		configureComponents();
		layoutComponents();
		installKeyMap();
		initializeData();
		weightsListModel.addListDataListener(this);
		weightTextField.requestFocus();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void requestFocus() {
		weightTextField.requestFocus();
	}

	/**
	 * Copy text from text area into copy and paste manager.
	 */
	public void copyAction() {
		CopyAndPaste.instance().copy(weightTextField.getText());
	}

	/**
	 * Get the text from the copy and paste manager and copy it into the text area,
	 */
	public void pasteAction() {
		String text = (String) CopyAndPaste.instance().paste();
		weightTextField.setText(text);
	}

	/**
	 * Delete text from text area. But first copy the contents to the copy and paste
	 * manager.
	 */
	public void deleteAction() {
		copyAction();
		weightTextField.setText("");
	}

	private void configureComponents() {
		LOGGER.entering(CLASS_NAME, "configureComponents");
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
				actionFactory.deleteAction().setEnabled(!weightTextField.getText().isEmpty());
				actionFactory.copyAction().setEnabled(!weightTextField.getText().isEmpty());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				addButton.setEnabled(validateWeight());
				actionFactory.deleteAction().setEnabled(!weightTextField.getText().isEmpty());
				actionFactory.copyAction().setEnabled(!weightTextField.getText().isEmpty());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				addButton.setEnabled(validateWeight());
				actionFactory.deleteAction().setEnabled(!weightTextField.getText().isEmpty());
				actionFactory.copyAction().setEnabled(!weightTextField.getText().isEmpty());
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
		deleteButton.addActionListener((event) -> {
			deleteButtonActionPerformed(event);
		});
		deleteButton.setEnabled(false);
		LOGGER.exiting(CLASS_NAME, "configureComponents");
	}

	private void layoutComponents() {
		LOGGER.entering(CLASS_NAME, "layoutComponents");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 10, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		add(fileLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 10, 10, 0);
		add(fileTextArea, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 10, 0, 5);
		add(weightCalendar, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(10, 10, 0, 0);
		add(weightLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(10, 5, 0, 0);
		add(weightTextField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(addButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 0, 0);
		add(weightsListLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridheight = 3;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		add(weightsScrollPane, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		add(deleteButton, gbc);
		LOGGER.exiting(CLASS_NAME, "layoutComponents");
	}

	private void weightCalendarPropertyChange(PropertyChangeEvent event) {
		LOGGER.entering(CLASS_NAME, "weightCalendarPropertyChange");
		selectedDate = LocalDate.now();
		if (event.getNewValue() != null) {
			if (event.getNewValue() instanceof Calendar) {
				Calendar cal = (Calendar) event.getNewValue();
				selectedDate = LocalDate.ofInstant(cal.toInstant(), ZoneId.systemDefault());
				scrollToSelectedDate();
			}
		}
		addButton.setEnabled(validateWeight());
		LOGGER.exiting(CLASS_NAME, "weightCalendarPropertyChange");
	}

	private void weightTextFieldActionPerformed(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "weightTextFieldActionPerformed");
		addButton.doClick();
		LOGGER.exiting(CLASS_NAME, "weightTextFieldActionPerformed");
	}

	private void weightsListValueChanged(ListSelectionEvent event) {
		LOGGER.entering(CLASS_NAME, "weightsListValueChanged");
		if (!event.getValueIsAdjusting()) {
			if (weightsList.getSelectedIndex() == -1) {
				deleteButton.setEnabled(false);
			} else {
				deleteButton.setEnabled(true);
			}
		}
		LOGGER.exiting(CLASS_NAME, "weightsListValueChanged");
	}

	private boolean validateWeight() {
		LOGGER.entering(CLASS_NAME, "validateWeight");
		String match = "^\\d*\\.?\\d+|\\d+\\.\\d*$";
		String sel = weightTextField.getText();
		boolean result = false;
		if (sel == null || sel.isEmpty()) {
			LOGGER.exiting(CLASS_NAME, "validateWeight", false);
			return false;
		}
		result = sel.matches(match);
		LOGGER.exiting(CLASS_NAME, "validateWeight", result);
		return result;
	}

	private void addButtonActionPerformed(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "addButtonActionPerformed");
		String weight = weightTextField.getText();
		Calendar cal = weightCalendar.getCalendar();
		selectedDate = LocalDate.ofInstant(cal.toInstant(), ZoneId.systemDefault());
		double dweight = Double.parseDouble(weight);
		weight = String.format("%.1f", dweight);
		Reading newReading = new Reading(selectedDate, weight);
		AddReadingChange addReadingChange = new AddReadingChange(newReading);
		submitChange(addReadingChange);
		LOGGER.exiting(CLASS_NAME, "addButtonActionPerformed");
	}

	private void submitChange(Change change) {
		LOGGER.entering(CLASS_NAME, "submitChange");
		ThreadServices.instance().executor().submit(() -> {
			ChangeManager.instance().execute(change);
		});
		LOGGER.exiting(CLASS_NAME, "submitChange");
	}

	private void deleteButtonActionPerformed(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "deleteButtonActionPerformed");
		Reading selectedReading = ReadingsManager.instance().reading(weightsList.getSelectedIndex());
		RemoveReadingChange change = new RemoveReadingChange(selectedReading);
		submitChange(change);
		LOGGER.exiting(CLASS_NAME, "deleteButtonActionPerformed");
	}

	private void installKeyMap() {
		LOGGER.entering(CLASS_NAME, "installKeyMap");
		weightTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK),
				actionFactory.undoAction());
		weightTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK),
				actionFactory.redoAction());
		weightTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),
				actionFactory.copyAction());
		weightTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),
				actionFactory.pasteAction());
		weightTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),
				actionFactory.deleteAction());
		LOGGER.exiting(CLASS_NAME, "installKeyMap");
	}

	private void initializeData() {
		LOGGER.entering(CLASS_NAME, "initializeData");
		weightCalendar.setDate(new Date());
		weightsList.setModel(weightsListModel);
		fileTextArea.setText(ReadingsManager.instance().dataFile().getAbsolutePath());
		weightsList.ensureIndexIsVisible(ReadingsManager.instance().readings().size() - 1);
		LOGGER.exiting(CLASS_NAME, "initializeData");
	}

	private void scrollToSelectedDate() {
		LOGGER.entering(CLASS_NAME, "scrollToSelectedDate");
		int selection = -1;
		for (int i = 0; i < ReadingsManager.instance().numberOfReadings(); i++) {
			Reading reading = ReadingsManager.instance().reading(i);
			if (reading.date().isAfter(selectedDate) || reading.date().equals(selectedDate)) {
				selection = i;
				break;
			}
		}
		if (selection == -1) {
			selection = ReadingsManager.instance().numberOfReadings() - 1;
		}
		weightsList.ensureIndexIsVisible(selection);
		weightsList.setSelectedIndex(selection);
		LOGGER.exiting(CLASS_NAME, "scrollToSelectedDate");
	}

	// ListDataListener implementation

	@Override
	public void intervalAdded(ListDataEvent e) {
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		LOGGER.entering(CLASS_NAME, "contentsChanged");
		weightsList.ensureIndexIsVisible(ReadingsManager.instance().readings().size() - 1);
		LOGGER.exiting(CLASS_NAME, "contentsChanged");
	}

}
