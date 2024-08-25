package app.weight.monitor.application.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import app.weight.monitor.BarChartModel;
import app.weight.monitor.application.WeightMonitorApplication;
import application.base.app.gui.ColorProvider;
import application.charting.ChartPainter;

public class BarChartPainter extends ChartPainter {

	private Rectangle2D.Double plotFrame;
	private int lSize;
	private int[] count;
	private double[] value;
	private double columnWidth;

	double minValue, maxValue;
	double gridSpacing, wLegend;
	int intervals;

	private BarChartModel model;

	public BarChartPainter(BarChartModel model) {
		super();
		this.model = model;
	}

	@Override
	public int indexOfEntryAt(MouseEvent me) {
		System.out.println("indexOfEntryAt");
		return 0;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		System.out.println("paint");
		lSize = values.length;
		System.out.println(lSize);
		if (lSize < 2) {
			return;
		}

		Graphics2D g2D = (Graphics2D) g;
		drawGraph(g2D);
		g2D.dispose();
	}

	private void drawGraph(Graphics2D g2D) {
		initializeValues();
		drawFrame(g2D);
		calculateMinAndMaxPoints();
		adjustMinAndMax();
		calculateSpacing();
		calculateIntervals();
		drawBarChart(g2D);
//		drawYLabelsAndGridLines(g2D);
//		drawBottomInfo(g2D);
//		drawTrendLine(g2D);
//		drawTitle(g2D);
	}

	private void drawFrame(Graphics2D g2D) {
		plotFrame = new Rectangle2D.Double(50, 40, 420, 280);
		g2D.setPaint(ColorProvider.get(WeightMonitorApplication.colorChoice.background()));
		g2D.fill(plotFrame);
		g2D.setStroke(new BasicStroke(2));
		g2D.setPaint(Color.black);
		g2D.draw(plotFrame);
	}

	private void initializeValues() {
		value = new double[lSize];
		count = new int[lSize];
		minValue = 1000000;
		maxValue = 0;
		for (int i = 0; i < lSize; i++) {
			count[i] = model.numberForValue(model.valueAtColumn(i));
		}
		columnWidth = 420 / lSize;

	}

	private void calculateMinAndMaxPoints() {
		printArray(values);
		printArray(count);
		for (int i = 0; i < lSize; i++) {
			value[i] = values[i];
			minValue = Math.min(count[i], minValue);
			maxValue = Math.max(count[i], maxValue);
		}
	}

	private void adjustMinAndMax() {
		if (minValue == maxValue) {
			minValue = maxValue - 1;
		}
	}

	private void calculateSpacing() {
		if (maxValue - minValue <= 5.0)
			gridSpacing = 1.0;
		else if (maxValue - minValue <= 10.0)
			gridSpacing = 2.0;
		else if (maxValue - minValue <= 25.0)
			gridSpacing = 5.0;
		else if (maxValue - minValue <= 50.0)
			gridSpacing = 10.0;
		else
			gridSpacing = 20.0;
	}

	private void calculateIntervals() {
		if (maxValue % (int) gridSpacing != 0)
			maxValue = gridSpacing * (int) (maxValue / gridSpacing) + gridSpacing;
		if (minValue % (int) gridSpacing != 0)
			minValue = gridSpacing * (int) (minValue / gridSpacing);
		intervals = (int) ((maxValue - minValue) / gridSpacing);
	}

	private void drawBarChart(Graphics2D g2D) {
		// draw bar chart
		g2D.setPaint(ColorProvider.get(WeightMonitorApplication.colorChoice.chartLine()));
		g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

		for (int i = 0; i < lSize; i++) {
			Rectangle2D.Double bar = new Rectangle2D.Double(columnToX(i), countToY(count[i], minValue, maxValue),
					columnWidth, countToH(count[i], minValue, maxValue));
			g2D.fill(bar);
		}
	}

	private double columnToX(int column) {
		return plotFrame.getX() + (columnWidth * column);
	}

	private double countToY(int count, double minCount, double maxCount) {
		return (maxCount - count) * (plotFrame.getHeight() - 1) / (maxCount - minCount) + plotFrame.getY();
	}

	private double countToH(int count, double minCount, double maxCount) {
		return plotFrame.getY() + plotFrame.getHeight() - countToY(count, minCount, maxCount);
	}

	private void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	private void printArray(double[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

}
