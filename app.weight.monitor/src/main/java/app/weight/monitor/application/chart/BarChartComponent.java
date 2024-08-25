package app.weight.monitor.application.chart;

import javax.swing.table.TableModel;

import app.weight.monitor.BarChartModel;
import application.charting.ChartComponent;
import application.charting.ChartPainter;

/**
 * Graphics class that controls a bar chart painter.
 */
public class BarChartComponent extends ChartComponent {
	private static final long serialVersionUID = 1L;

	protected double[] values;

	/**
	 * Create the component.
	 * 
	 * @param tableModel   - the model that supplies the data.
	 * @param chartPainter - the painter that prints the screen.
	 */
	public BarChartComponent(TableModel tableModel, ChartPainter chartPainter) {
		super(tableModel, chartPainter);
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		System.out.println("updateLocalValues");
		if (freshStart) {
			int count = ((BarChartModel) model).numberOfColumns();
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
	}

	/**
	 * Create and store the tool tips.
	 */
	protected void createLabelsAndTips() {
		for (int i = ((BarChartModel) model).numberOfColumns() - 1; i >= 0; i--) {
			tips[i] = (((BarChartModel) model).numberForValue(i)) + " @ " + ((BarChartModel) model).valueAtColumn(i);
		}
	}

	/**
	 * Store the values from the model.
	 */
	private void calculateValues() {
		int lSize = ((BarChartModel) model).numberOfColumns();
		for (int i = 0; i < lSize; i++) {
			double value = ((BarChartModel) model).valueAtColumn(i);
			values[i] = value;
			labels[i] = Integer.toString(((BarChartModel) model).numberForValue(value));
		}
	}

}
