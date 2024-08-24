package app.weight.monitor.application.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import app.weight.monitor.application.WeightMonitorApplication;
import application.base.app.gui.ColorProvider;
import application.charting.ChartPainter;

public class BarChartPainter extends ChartPainter {

	protected static BarChartPainter chartUI = new BarChartPainter();
	private Rectangle2D.Double plotFrame;
	private int lSize;
	private int[] count;
	private double[] value;

	int minValue, maxValue;

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
//		adjustMinAndMax();
//		calculateSpacing();
//		calculateIntervals();
//		drawPlotLine(g2D);
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
		minValue = 1000000;
		maxValue = 0;
	}

	private void calculateMinAndMaxPoints() {
		printArray(count);
		printArray(values);
		for (int i = 0; i < lSize; i++) {
			value[i] = values[i];
			minValue = Math.min(count[i], minValue);
			maxValue = Math.max(count[i], maxValue);
		}
		System.out.println(minValue + " " + maxValue);
	}

	public void setCounts(int[] counts) {
		printArray(counts);
		count = Arrays.copyOf(counts, counts.length);
		printArray(count);
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

	public static ComponentUI createUI(JComponent c) {
		return chartUI;
	}

}
