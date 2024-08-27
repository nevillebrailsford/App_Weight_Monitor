package app.weight.monitor.application.chart;

import java.util.logging.Logger;

import javax.swing.table.TableModel;

import application.charting.ChartComponent;
import application.charting.ChartPainter;
import application.definition.ApplicationConfiguration;

/**
 * Graphics class that controls a line graph painter.
 */
public class LineChartComponent extends ChartComponent {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = LineChartComponent.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	protected double[] values;
	protected String[] dates;

	/**
	 * Create the component.
	 * 
	 * @param tableModel   - the model that supplies the data.
	 * @param chartPainter - the painter that prints the screen.
	 */
	public LineChartComponent(TableModel tableModel, ChartPainter chartPainter) {
		super(tableModel, chartPainter);
		LOGGER.entering(CLASS_NAME, "cinit");
		tableModel.addTableModelListener(this);
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	/**
	 * Create and store the tool tips.
	 */
	protected void createLabelsAndTips() {
		LOGGER.entering(CLASS_NAME, "createLabelsAndTips");
		LineChartPainter lcp = (LineChartPainter) cp;
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			tips[i] = String
					.valueOf(model.getValueAt(i, 1) + " on " + lcp.convertToUKFormat((String) model.getValueAt(i, 0)));
		}
		LOGGER.exiting(CLASS_NAME, "createLabelsAndTips");
	}

	/**
	 * Store the values from the model.
	 */
	protected void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		int lSize = model.getRowCount();
		for (int i = 0; i < lSize; i++) {
			labels[i] = (String) model.getValueAt(i, 0);
			values[i] = Double.parseDouble(((String) model.getValueAt(i, 1)));
		}
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		LOGGER.entering(CLASS_NAME, "updateLocalValues", freshStart);
		int count = model.getRowCount();
		if (freshStart) {
			values = new double[count];
			labels = new String[count];
			tips = new String[count];
		} else {
			if (values == null || count != values.length) {
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

}
