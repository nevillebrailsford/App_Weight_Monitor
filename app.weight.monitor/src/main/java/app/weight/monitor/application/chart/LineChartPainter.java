package app.weight.monitor.application.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import app.weight.monitor.application.WeightMonitorApplication;
import application.base.app.gui.ColorProvider;
import application.charting.ChartPainter;
import application.definition.ApplicationConfiguration;

/**
 * Graphics class that draws a straight line graph.
 */
public class LineChartPainter extends ChartPainter {

	private static final String CLASS_NAME = LineChartPainter.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	protected static LineChartPainter chartUI = new LineChartPainter();
	private Rectangle2D.Double plotFrame;
	private int lSize;
	private double[] date;
	private double[] value;

	double minValue, maxValue;
	String s;
	int intervals;
	double gridSpacing, wLegend;
	double sumD, sumD2, sumW, sumDW;
	double tSumW, tSumDW;
	double t, wo;
	long t1, t2;
	Font labelFont;
	Rectangle2D labelRect;
	String lblText;

	@Override
	public int indexOfEntryAt(MouseEvent me) {
		LOGGER.entering(CLASS_NAME, "indexOfEntryAt");
		int x = me.getX();
		double d = xToD(x, date[lSize - 1]);
		int index = -1;
		for (int i = date.length - 1; i >= 0; i--) {
			if (d >= date[i]) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "indexOfEntryAt", index);
		return index;
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
		LOGGER.entering(CLASS_NAME, "drawGraph");
		initializeValues();
		drawFrame(g2D);
		calculateMinAndMaxPoints();
		adjustMinAndMax();
		calculateSpacing();
		calculateIntervals();
		drawPlotLine(g2D);
		drawYLabelsAndGridLines(g2D);
		drawBottomInfo(g2D);
		drawTrendLine(g2D);
		drawTitle(g2D);
		LOGGER.exiting(CLASS_NAME, "drawGraph");
	}

	private void initializeValues() {
		LOGGER.entering(CLASS_NAME, "initializeValues");
		date = new double[lSize];
		value = new double[lSize];
		minValue = 1000000.0;
		maxValue = 0.0;
		sumD = 0.0;
		sumD2 = 0.0;
		sumW = 0.0;
		tSumW = 0.0;
		tSumDW = 0.0;
		sumDW = 0.0;
		labelFont = new Font("Arial", Font.BOLD, 14);
		plotFrame = new Rectangle2D.Double(50, 40, 420, 280);
		LOGGER.exiting(CLASS_NAME, "initializeValues");
	}

	private void drawFrame(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawFrame");
		g2D.setPaint(ColorProvider.get(WeightMonitorApplication.colorChoice.background()));
		g2D.fill(plotFrame);
		g2D.setStroke(new BasicStroke(2));
		g2D.setPaint(Color.black);
		g2D.draw(plotFrame);
		LOGGER.exiting(CLASS_NAME, "drawFrame");
	}

	private void calculateMinAndMaxPoints() {
		LOGGER.entering(CLASS_NAME, "calculateMinAndMaxPoints");
		t1 = stringToDate(labels[0]).getTime();
		for (int i = 0; i < lSize; i++) {
			s = labels[i];
			t2 = stringToDate(s).getTime();
			date[i] = (double) ((t2 - t1) / (24 * 3600 * 1000));
			value[i] = values[i];
			minValue = Math.min(value[i], minValue);
			maxValue = Math.max(value[i], maxValue);
			sumD += date[i];
			sumD2 += date[i] * date[i];
			sumW += value[i];
			sumDW += date[i] * value[i];
			tSumW += values[i];
			tSumDW += date[i] * values[i];
		}
		LOGGER.exiting(CLASS_NAME, "calculateMinAndMaxPoints", new Object[] { minValue, maxValue });
	}

	private void adjustMinAndMax() {
		LOGGER.entering(CLASS_NAME, "adjustMinAndMax");
		if (minValue == maxValue) {
			minValue = maxValue - 1;
		}
		maxValue = (double) ((int) maxValue + 0.5);
		minValue = (double) ((int) minValue - 0.5);
		LOGGER.exiting(CLASS_NAME, "adjustMinAndMax", new Object[] { minValue, maxValue });
	}

	private void calculateSpacing() {
		LOGGER.entering(CLASS_NAME, "calculateSpacing");
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
		LOGGER.exiting(CLASS_NAME, "calculateSpacing", gridSpacing);
	}

	private void calculateIntervals() {
		LOGGER.entering(CLASS_NAME, "calculateIntervals");
		if (maxValue % (int) gridSpacing != 0)
			maxValue = gridSpacing * (int) (maxValue / gridSpacing) + gridSpacing;
		if (minValue % (int) gridSpacing != 0)
			minValue = gridSpacing * (int) (minValue / gridSpacing);
		intervals = (int) ((maxValue - minValue) / gridSpacing);
		LOGGER.exiting(CLASS_NAME, "calculateIntervals", intervals);
	}

	private void drawPlotLine(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawPlotLine");
		// draw plot
		g2D.setPaint(ColorProvider.get(WeightMonitorApplication.colorChoice.chartLine()));
		g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		for (int i = 1; i < lSize; i++) {
			Line2D.Double weightLine = new Line2D.Double(dToX(date[i - 1], date[lSize - 1]),
					wToY(value[i - 1], minValue, maxValue), dToX(date[i], date[lSize - 1]),
					wToY(value[i], minValue, maxValue));
			g2D.draw(weightLine);
		}
		LOGGER.exiting(CLASS_NAME, "drawPlotLine");
	}

	private void drawYLabelsAndGridLines(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawYLabelsAndGridLines");
		// Draw y labels and grid lines
		g2D.setFont(labelFont);
		g2D.setStroke(new BasicStroke(1));
		g2D.setPaint(Color.black);
		wLegend = minValue;
		for (int i = 0; i <= intervals; i++) {
			lblText = String.valueOf((int) wLegend);
			labelRect = labelFont.getStringBounds(lblText, g2D.getFontRenderContext());
			g2D.drawString(lblText, (int) (plotFrame.getX() - labelRect.getWidth() - 5),
					(int) (wToY(wLegend, minValue, maxValue) + 0.5 * labelRect.getHeight()));
			if (i > 0 && i < intervals) {
				Line2D.Double gridLine = new Line2D.Double(plotFrame.getX(), wToY(wLegend, minValue, maxValue),
						plotFrame.getX() + plotFrame.getWidth(), wToY(wLegend, minValue, maxValue));
				g2D.draw(gridLine);
			}
			wLegend += gridSpacing;
		}
		LOGGER.exiting(CLASS_NAME, "drawYLabelsAndGridLines");
	}

	private void drawBottomInfo(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawBottomInfo");
		// Draw bottom into
		g2D.setPaint(Color.black);
		String dateText = "Start: " + convertToUKFormat(labels[0]);
		g2D.drawString(dateText, (int) (plotFrame.getX() + 10), (int) (plotFrame.getY() + plotFrame.getHeight() + 20));
		Line2D.Double dateLine = new Line2D.Double(plotFrame.getX() + 10, plotFrame.getY() + plotFrame.getHeight() + 10,
				plotFrame.getX(), plotFrame.getY() + plotFrame.getHeight());
		g2D.draw(dateLine);
		dateText = "End: " + convertToUKFormat(labels[labels.length - 1]);
		Rectangle2D dateRect = labelFont.getStringBounds(dateText, g2D.getFontRenderContext());
		g2D.drawString(dateText, (int) (plotFrame.getX() + plotFrame.getWidth() - dateRect.getWidth() - 10),
				(int) (plotFrame.getY() + plotFrame.getHeight() + 20));
		dateLine = new Line2D.Double(plotFrame.getX() + plotFrame.getWidth() - 10,
				plotFrame.getY() + plotFrame.getHeight() + 10, plotFrame.getX() + plotFrame.getWidth(),
				plotFrame.getY() + plotFrame.getHeight());
		g2D.draw(dateLine);
		LOGGER.exiting(CLASS_NAME, "drawBottomInfo");
	}

	private void drawTrendLine(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawTrendLine");
		// draw trend line and title
		t = (lSize * sumDW - sumD * sumW) / (lSize * sumD2 - sumD * sumD);
		wo = (sumD2 * sumW - sumD * sumDW) / (lSize * sumD2 - sumD * sumD);
		Line2D.Double trendLine = new Line2D.Double(plotFrame.getX(), wToY(wo, minValue, maxValue),
				dToX(date[lSize - 1], date[lSize - 1]), wToY(t * date[lSize - 1] + wo, minValue, maxValue));
		g2D.setStroke(new BasicStroke(1));
		g2D.setPaint(ColorProvider.get(WeightMonitorApplication.colorChoice.trendLine()));
		g2D.draw(trendLine);
		LOGGER.exiting(CLASS_NAME, "drawTrendLine");
	}

	private void drawTitle(Graphics2D g2D) {
		LOGGER.entering(CLASS_NAME, "drawTitle");
		String title = "Trend: ";
		if (t > 0)
			title += "+";
		t = (lSize * tSumDW - sumD * tSumW) / (lSize * sumD2 - sumD * sumD);
		title += new DecimalFormat("0.00").format(7 * t) + " kg/week";
		Font titleFont = new Font("Arial", Font.BOLD, 16);
		Rectangle2D titleRect = titleFont.getStringBounds(title, g2D.getFontRenderContext());
		g2D.setFont(titleFont);
		g2D.setPaint(Color.black);
		g2D.drawString(title, (int) (plotFrame.getX() + 0.5 * (plotFrame.getWidth() - titleRect.getWidth())),
				(int) (plotFrame.getY() - 10));
		LOGGER.exiting(CLASS_NAME, "drawTitle");
	}

	public static ComponentUI createUI(JComponent c) {
		return chartUI;
	}

	public static Date stringToDate(String s) {
		LOGGER.entering(CLASS_NAME, "stringToDate", s);
		int y = Integer.valueOf(s.substring(0, 4)).intValue();
		int m = Integer.valueOf(s.substring(5, 7)).intValue();
		int d = Integer.valueOf(s.substring(8, 10)).intValue();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);
		Date result = new Date(cal.getTimeInMillis());
		LOGGER.exiting(CLASS_NAME, "stringToDate", result);
		return result;
	}

	private int dToX(double d, double dmax) {
		return (int) (d * (plotFrame.getWidth() - 1) / dmax + plotFrame.getX());
	}

	private int wToY(double w, double wmin, double wmax) {
		return (int) ((wmax - w) * (plotFrame.getHeight() - 1) / (wmax - wmin) + plotFrame.getY());
	}

	private double xToD(int x, double dmax) {
		return dmax * (x - plotFrame.getX()) / (plotFrame.getWidth() - 1);
	}

	/**
	 * Convert date to UK format
	 * 
	 * @param date - in US format
	 * @return date in UK format
	 */
	public String convertToUKFormat(String date) {
		LOGGER.entering(CLASS_NAME, "convertToUKFormat", date);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		DateTimeFormatter infoDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(date, dateFormatter);
		String result = localDate.format(infoDateFormatter);
		LOGGER.exiting(CLASS_NAME, "convertToUKFormat", result);
		return result;
	}
}
