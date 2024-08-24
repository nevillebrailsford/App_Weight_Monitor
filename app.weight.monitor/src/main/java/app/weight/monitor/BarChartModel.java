package app.weight.monitor;

import javax.swing.table.TableModel;

/**
 * Provide the model interface to be used by bar charts.
 */
public interface BarChartModel extends TableModel {

	/**
	 * Calculate the number of columns needed.
	 * 
	 * @return the number of columns.
	 */
	int numberOfColumns();

	/**
	 * Return the value at the column at columnNumber.
	 * 
	 * @param columnNumber - the column number
	 * @return the value at that column.
	 */
	double valueAtColumn(int columnNumber);

	/**
	 * Calculate the number of entries for this value.
	 * 
	 * @param value - the value to be counted.
	 * @return the number of times this value is recorded.
	 */
	int numberForValue(double value);
}
