package app.weight.monitor.application.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.JComponent;

import app.weight.monitor.BarChartModel;
import app.weight.monitor.application.WeightMonitorApplication;
import app.weight.monitor.storage.ReadingsManager;
import application.base.app.gui.ColorProvider;
import application.charting.ChartPainter;
import application.definition.ApplicationConfiguration;

public class BarChartPainter extends ChartPainter {

	private static final String CLASS_NAME = BarChartPainter.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Rectangle2D.Double plotFrame;
	private int lSize;
	private int[] count;
	private Color[] color;
	private double[] value;
	private double columnWidth;

	double minValue, maxValue;
	double gridSpacing;
	int intervals, wLegend;
	Font labelFont;
	Rectangle2D labelRect;
	String lblText;

	private BarChartModel model;

	public BarChartPainter(BarChartModel model) {
		super();
		LOGGER.entering(CLASS_NAME, "cinit");
		this.model = model;
		LOGGER.exiting(CLASS_NAME, "cinit");
	}

	@Override
	public int indexOfEntryAt(MouseEvent me) {
		LOGGER.entering(CLASS_NAME, "indexOfEntryAt");
		int result = (int) Math.floor((me.getX() - plotFrame.getX()) / columnWidth);
		LOGGER.exiting(CLASS_NAME, "indexOfEntryAt", result);
		return result;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		LOGGER.entering(CLASS_NAME, "paint");
		lSize = values.length;
		if (lSize < 2) {
			return;
		}

		Graphics2D g2D = (Graphics2D) g;
		drawGraph(g2D);
		g2D.dispose();
		LOGGER.exiting(CLASS_NAME, "paint");
	}

	private void drawGraph(Graphics2D g2D) {
		initializeValues();
		drawFrame(g2D);
		calculateMinAndMaxPoints();
		adjustMinAndMax();
		calculateSpacing();
		calculateIntervals();
		drawYLabelsAndGridLines(g2D);
		drawBarChart(g2D);
//		drawBottomInfo(g2D);
//		drawTrendLine(g2D);
		drawTitle(g2D);
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
		minValue = 1000000;
		maxValue = 0;
		columnWidth = 420 / lSize;
		if (count == null || count.length != lSize) {
			count = new int[lSize];
		}
		for (int i = 0; i < lSize; i++) {
			count[i] = model.numberForValue(model.valueAtColumn(i));
		}
		if (color == null || color.length != lSize) {
			color = new Color[lSize];
			for (int i = 0; i < lSize; i++) {
				color[i] = ColorProvider.nextColor();
			}
		}
		labelFont = new Font("Arial", Font.BOLD, 14);
	}

	private void calculateMinAndMaxPoints() {
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
		g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		for (int i = 0; i < lSize; i++) {
			Rectangle2D.Double bar = new Rectangle2D.Double(columnToX(i), countToY(count[i], minValue, maxValue),
					columnWidth, countToH(count[i], minValue, maxValue));
			g2D.setPaint(color[i]);
			g2D.fill(bar);
			g2D.setPaint(Color.black);
			g2D.draw(bar);
		}
	}

	private void drawYLabelsAndGridLines(Graphics2D g2D) {
		// Draw y labels and grid lines
		g2D.setFont(labelFont);
		g2D.setStroke(new BasicStroke(1));
		g2D.setPaint(Color.black);
		wLegend = (int) minValue;
		for (int i = 0; i <= intervals; i++) {
			lblText = String.valueOf((int) wLegend);
			labelRect = labelFont.getStringBounds(lblText, g2D.getFontRenderContext());
			g2D.drawString(lblText, (int) (plotFrame.getX() - labelRect.getWidth() - 5),
					(int) (countToY(wLegend, minValue, maxValue) + 0.5 * labelRect.getHeight()));
			if (i > 0 && i < intervals) {
				Line2D.Double gridLine = new Line2D.Double(plotFrame.getX(), countToY(wLegend, minValue, maxValue),
						plotFrame.getX() + plotFrame.getWidth(), countToY(wLegend, minValue, maxValue));
				g2D.draw(gridLine);
			}
			wLegend += gridSpacing;
		}
	}

	private void drawTitle(Graphics2D g2D) {
		double standardDeviation = standardDeviation();
		String title = "Weight Distribution " + '\u03C3' + " " + String.format("%.5f", standardDeviation) + "kg";
		Font titleFont = new Font("Arial", Font.BOLD, 16);
		Rectangle2D titleRect = titleFont.getStringBounds(title, g2D.getFontRenderContext());
		g2D.setFont(titleFont);
		g2D.setPaint(Color.black);
		g2D.drawString(title, (int) (plotFrame.getX() + 0.5 * (plotFrame.getWidth() - titleRect.getWidth())),
				(int) (plotFrame.getY() - 10));
	}

	private double standardDeviation() {
		double mean = mean();
		double[] deviation = deviation(mean);
		double[] deviationSquared = deviationSquared(deviation);
		double sumOfDeviationSquared = sumOfDeviationSquared(deviationSquared);
		double variance = variance(sumOfDeviationSquared);
		double standardDeviation = Math.sqrt(variance);
		return standardDeviation;
	}

	private double mean() {
		return Double.parseDouble(ReadingsManager.instance().averageWeight());
	}

	private double[] deviation(double mean) {
		int numberOfReadings = ReadingsManager.instance().numberOfReadings();
		double[] deviations = new double[numberOfReadings];
		for (int i = 0; i < numberOfReadings; i++) {
			deviations[i] = Double.parseDouble(ReadingsManager.instance().reading(i).weight()) - mean;
		}
		return deviations;
	}

	private double[] deviationSquared(double[] deviation) {
		double[] deviationSquared = new double[deviation.length];
		for (int i = 0; i < deviation.length; i++) {
			deviationSquared[i] = deviation[i] * deviation[i];
		}
		return deviationSquared;
	}

	private double sumOfDeviationSquared(double[] deviationSquared) {
		double result = 0.0;
		for (int i = 0; i < deviationSquared.length; i++) {
			result += deviationSquared[i];
		}
		return result;
	}

	private double variance(double sumOfDeviationSquared) {
		return sumOfDeviationSquared / (ReadingsManager.instance().numberOfReadings() - 1);
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
