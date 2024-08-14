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

import app.weight.monitor.actions.ActionFactory;
import app.weight.monitor.application.IApplication;
import app.weight.monitor.change.AddReadingChange;
import app.weight.monitor.change.RemoveReadingChange;
import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.gui.ColoredPanel;
import application.change.Change;
import application.change.ChangeManager;
import application.replicate.CopyAndPaste;
import application.thread.ThreadServices;

/**
 * Main panel for entering data.
 */
public class EditorPanel extends ColoredPanel implements ListDataListener {
	private static final long serialVersionUID = 1L;

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

	private ActionFactory actionFactory = null;

	/**
	 * Create the editor panel for this application.
	 * 
	 * @param application - the application that this panel is used by.
	 */
	public EditorPanel(IApplication application) {
		super();
		actionFactory = ActionFactory.instance(application);
		weightsListModel = ReadingsManager.instance();
		ReadingsManager.instance().addListDataListener(this);
		setLayout(new GridBagLayout());
		configureComponents();
		layoutComponents();
		installKeyMap();
		initializeData();
		weightsListModel.addListDataListener(this);
		weightTextField.requestFocus();
	}

	@Override
	public void requestFocus() {
		weightTextField.requestFocus();
	}

	public void undoAction() {
		ChangeManager.instance().undo();
	}

	public void redoAction() {
		ChangeManager.instance().redo();
	}

	public void copyAction() {
		CopyAndPaste.instance().copy(weightTextField.getText());
	}

	public void pasteAction() {
		String text = (String) CopyAndPaste.instance().paste();
		weightTextField.setText(text);
	}

	public void deleteAction() {
		weightTextField.setText("");
	}

	private void configureComponents() {
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
		deleteButton.addActionListener((event) -> {
			deleteButtonActionPerformed(event);
		});
		deleteButton.setEnabled(false);
	}

	private void layoutComponents() {
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
	}

	private void weightCalendarPropertyChange(PropertyChangeEvent event) {
		selectedDate = LocalDate.now();
		if (event.getNewValue() != null) {
			if (event.getNewValue() instanceof Calendar) {
				Calendar cal = (Calendar) event.getNewValue();
				selectedDate = LocalDate.ofInstant(cal.toInstant(), ZoneId.systemDefault());
			}
		}
		addButton.setEnabled(validateWeight());
	}

	private void weightTextFieldActionPerformed(ActionEvent event) {
		addButton.doClick();
	}

	private void weightsListValueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			if (weightsList.getSelectedIndex() == -1) {
				deleteButton.setEnabled(false);
				actionFactory.deleteAction().setEnabled(false);
			} else {
				deleteButton.setEnabled(true);
				actionFactory.deleteAction().setEnabled(true);
			}
		}
	}

	private boolean validateWeight() {
		String match = "^\\d*\\.?\\d+|\\d+\\.\\d*$";
		String sel = weightTextField.getText();
		if (sel == null || sel.isEmpty()) {
			return false;
		}
		return sel.matches(match);
	}

	private void addButtonActionPerformed(ActionEvent event) {
		String weight = weightTextField.getText();
		Calendar cal = weightCalendar.getCalendar();
		selectedDate = LocalDate.ofInstant(cal.toInstant(), ZoneId.systemDefault());
		Reading newReading = new Reading(selectedDate, weight);
		AddReadingChange addReadingChange = new AddReadingChange(newReading);
		submitChange(addReadingChange);
	}

	private void submitChange(Change change) {
		ThreadServices.instance().executor().submit(() -> {
			ChangeManager.instance().execute(change);
		});
	}

	private void deleteButtonActionPerformed(ActionEvent event) {
		Reading selectedReading = ReadingsManager.instance().reading(weightsList.getSelectedIndex());
		RemoveReadingChange change = new RemoveReadingChange(selectedReading);
		submitChange(change);
	}

	private void installKeyMap() {
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
	}

	private void initializeData() {
		weightCalendar.setDate(new Date());
		weightsList.setModel(weightsListModel);
		fileTextArea.setText(ReadingsManager.instance().dataFile().getAbsolutePath());
		weightsList.ensureIndexIsVisible(ReadingsManager.instance().readings().size() - 1);
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
		weightsList.ensureIndexIsVisible(ReadingsManager.instance().readings().size() - 1);
	}

}
