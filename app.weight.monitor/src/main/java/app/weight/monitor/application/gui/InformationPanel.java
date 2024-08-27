package app.weight.monitor.application.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import app.weight.monitor.InformationModel;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;

public class InformationPanel extends ColoredPanel implements TableModelListener {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = InformationPanel.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private JTextField numberOfReadings = new JTextField();
	private JTextField numberOfWeeks = new JTextField();
	private JTextField earliestDate = new JTextField();
	private JTextField latestDate = new JTextField();
	private JTextField earliestWeight = new JTextField();
	private JTextField latestWeight = new JTextField();
	private JTextField lightestWeight = new JTextField();
	private JTextField heaviestWeight = new JTextField();
	private JTextField changeInWeight = new JTextField();
	private JTextField averageWeight = new JTextField();
	private JTextField averageChangePerWeek = new JTextField();

	private int yRow = 0;
	private int topGap = 35;

	public InformationPanel() {
		LOGGER.entering(CLASS_NAME, "cinit");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);

		buildGUI();
		loadData();
		ReadingsManager.instance().addTableModelListener(this);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	private void buildGUI() {
		LOGGER.entering(CLASS_NAME, "buildGUI");
		buildLabel("Number of Readings:");
		buildTextField(numberOfReadings);
		topGap = 5;

		buildLabel("Number of Weeks:");
		buildTextField(numberOfWeeks);

		buildLabel("Earliest Reading:");
		buildTextField(earliestDate);

		buildLabel("Latest Reading:");
		buildTextField(latestDate);

		buildLabel("Earliest Weight:");
		buildTextField(earliestWeight);

		buildLabel("Latest Weight:");
		buildTextField(latestWeight);

		buildLabel("Lightest Weight :");
		buildTextField(lightestWeight);

		buildLabel("Heaviest Weight:");
		buildTextField(heaviestWeight);

		buildLabel("Change In Weight:");
		buildTextField(changeInWeight);

		buildLabel("Average Weight:");
		buildTextField(averageWeight);

		buildLabel("Average Change Per Week:");
		buildTextField(averageChangePerWeek);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	private void buildTextField(JTextField textField) {
		LOGGER.entering(CLASS_NAME, "buildTextField");
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setFont(new Font("Arial", Font.PLAIN, 12));
		textField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(topGap, 30, 5, 120);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = yRow - 1;
		add(textField, gbc_textField);
		LOGGER.exiting(CLASS_NAME, "buildTextField");
	}

	private void buildLabel(String title) {
		LOGGER.entering(CLASS_NAME, "buildLabel");
		JLabel lblNewLabel = new JLabel(title);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(topGap, 60, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = yRow++;
		add(lblNewLabel, gbc_lblNewLabel);
		LOGGER.exiting(CLASS_NAME, "buildLabel");
	}

	private void loadData() {
		LOGGER.exiting(CLASS_NAME, "loadData");
		InformationModel info = ReadingsManager.instance();
		numberOfReadings.setText(Integer.toString(info.numberOfReadings()));
		numberOfWeeks.setText(Integer.toString(info.numberOfWeeks()));
		earliestDate.setText(info.earliestDate());
		latestDate.setText(info.latestDate());
		earliestWeight.setText(info.earliestWeight());
		latestWeight.setText(info.latestWeight());
		lightestWeight.setText(info.lightestWeight());
		heaviestWeight.setText(info.heaviestWeight());
		changeInWeight.setText(info.changeInWeight());
		averageWeight.setText(info.averageWeight());
		averageChangePerWeek.setText(info.averageChangePerWeek());
		LOGGER.exiting(CLASS_NAME, "loadData");
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		LOGGER.entering(CLASS_NAME, "tableChanged");
		loadData();
		LOGGER.exiting(CLASS_NAME, "tableChanged");
	}
}
