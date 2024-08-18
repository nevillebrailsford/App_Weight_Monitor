package app.weight.monitor.application.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import app.weight.monitor.InformationModel;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.gui.ColoredPanel;

public class InformationPanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

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

	public InformationPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);

		buildGUI();
		loadData();
	}

	private void buildGUI() {
		buildLabel("Number of Readings:");
		buildTextField(numberOfReadings);

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
	}

	private void buildTextField(JTextField textField) {
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setFont(new Font("Arial", Font.PLAIN, 12));
		textField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 30, 5, 150);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = yRow - 1;
		add(textField, gbc_textField);
	}

	private void buildLabel(String title) {
		JLabel lblNewLabel = new JLabel(title);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = yRow++;
		add(lblNewLabel, gbc_lblNewLabel);
	}

	private void loadData() {
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
	}
}
