package app.weight.monitor.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Reading stores the weight on a particular date.
 */
public class Reading implements Comparable<Reading> {

	private LocalDate date = null;
	private String weight = "";
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	/**
	 * Create a new reading.
	 * 
	 * @param date   - the date of the reading
	 * @param weight - the weight on that date
	 */
	public Reading(LocalDate date, String weight) {
		if (date == null) {
			throw new IllegalArgumentException("Reading: date is null");
		}
		if (weight == null) {
			throw new IllegalArgumentException("Reading: weight is null");
		}
		if (weight.isEmpty()) {
			throw new IllegalArgumentException("Reading: weight is null");
		}
		if (!weight.matches("\\d+(\\.\\d{1,2})?")) {
			throw new IllegalArgumentException("Reading: weight is not valid format");
		}
		this.date = date;
		this.weight = weight;
	}

	/**
	 * Get the date.
	 * 
	 * @return - the date of the reading.
	 */
	public LocalDate date() {
		return date;
	}

	/**
	 * Get the weight.
	 * 
	 * @return - the weight recorded.
	 */
	public String weight() {
		return weight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reading other = (Reading) obj;
		return Objects.equals(date, other.date);
	}

	@Override
	public String toString() {
		return date.format(dateFormatter) + "       " + weight;
	}

	@Override
	public int compareTo(Reading other) {
		return date.compareTo(other.date);
	}

}
