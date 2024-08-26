package app.weight.monitor.application.chart;

import java.util.logging.Logger;

import javax.swing.table.TableModel;

import app.weight.monitor.BarChartModel;
import application.charting.ChartComponent;
import application.charting.ChartPainter;
import application.definition.ApplicationConfiguration;

/**
 * Graphics class that controls a bar chart painter.
 */
public class BarChartComponent extends ChartComponent {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BarChartComponent.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	protected double[] values;

	/**
	 * Create the component.
	 * 
	 * @param tableModel   - the model that supplies the data.
	 * @param chartPainter - the painter that prints the screen.
	 */
	public BarChartComponent(TableModel tableModel, ChartPainter chartPainter) {
		super(tableModel, chartPainter);
		LOGGER.entering(CLASS_NAME, "cinit");
		tableModel.addTableModelListener(this);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		LOGGER.entering(CLASS_NAME, "updateLocalValues", freshStart);
		int count = ((BarChartModel) model).numberOfColumns();
		if (freshStart) {
			values = new double[count];
			labels = new String[count];
			tips = new String[count];
		} else {
			if (tips == null || count != tips.length) {
				values = new double[count];
				labels = new String[count];
				tips = new String[count];
			}
		}

		calculateValues();
		createLabelsAndTips();

		cp.setValues(values);
		cp.setLabels(labels);

		repaint();
		LOGGER.exiting(CLASS_NAME, "updateLocalValues");
	}

	/**
	 * Create and store the tool tips.
	 */
	protected void createLabelsAndTips() {
		LOGGER.entering(CLASS_NAME, "createLabelsAndTips");
		BarChartModel m = (BarChartModel) model;
		for (int i = m.numberOfColumns() - 1; i >= 0; i--) {
			tips[i] = m.numberForValue(m.valueAtColumn(i)) + " @ " + m.valueAtColumn(i);
		}
		LOGGER.exiting(CLASS_NAME, "createLabelsAndTips");
	}

	/**
	 * Store the values from the model.
	 */
	private void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		int lSize = ((BarChartModel) model).numberOfColumns();
		for (int i = 0; i < lSize; i++) {
			double value = ((BarChartModel) model).valueAtColumn(i);
			values[i] = value;
			labels[i] = Integer.toString(((BarChartModel) model).numberForValue(value));
		}
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

}
