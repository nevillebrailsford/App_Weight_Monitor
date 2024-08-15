package app.weight.monitor.application.chart;

import java.awt.BorderLayout;

import javax.swing.ToolTipManager;

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
	 * @param chartComponent - a chart component.
	 */
	public WeightGraph(ChartComponent chartComponent) {
		super();
		setLayout(new BorderLayout());
		add(chartComponent, BorderLayout.CENTER);
		ToolTipManager.sharedInstance().registerComponent(chartComponent);
	}

}
