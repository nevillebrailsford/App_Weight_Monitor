package app.weight.monitor.application.chart;

import javax.swing.table.TableModel;

import application.charting.ChartComponent;
import application.charting.ChartPainter;

/**
 * Graphics class that controls a line graph painter.
 */
public class LineChartComponent extends ChartComponent {
	private static final long serialVersionUID = 1L;

	protected double[] values;
	protected String[] dates;

	/**
	 * Create the component.
	 * 
	 * @param tableModel   - the model that supplies the data.
	 * @param chartPainter - the painter that psints the screen.
	 */
	public LineChartComponent(TableModel tableModel, ChartPainter chartPainter) {
		super(tableModel, chartPainter);
	}

	/**
	 * Create and store the tool tips.
	 */
	protected void createLabelsAndTips() {
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			tips[i] = String.valueOf(model.getValueAt(i, 1) + " on " + model.getValueAt(i, 0));
		}
	}

	/**
	 * Store the values from the model.
	 */
	protected void calculateValues() {
		int lSize = model.getRowCount();
		for (int i = 0; i < lSize; i++) {
			labels[i] = (String) model.getValueAt(i, 0);
			values[i] = Double.parseDouble(((String) model.getValueAt(i, 1)));
		}
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		if (freshStart) {
			int count = model.getRowCount();
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
	}

}
