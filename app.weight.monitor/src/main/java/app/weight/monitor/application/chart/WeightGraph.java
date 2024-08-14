package app.weight.monitor.application.chart;

import javax.swing.table.TableModel;

import application.base.app.gui.ColoredPanel;
import application.charting.ChartComponent;

/**
 * This class draws the graph showing my weight history.
 */
public class WeightGraph extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the drawing panel.
	 * 
	 * @param cp - a chart component.
	 * @param tm - a table model.
	 */
	public WeightGraph(ChartComponent cp, TableModel tm) {
		super();
	}

	/**
	 * Create the drawing panel.
	 */
	public WeightGraph() {
		super();
	}
}
