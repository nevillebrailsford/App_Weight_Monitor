package app.weight.monitor;

/**
 * Model to provide various information about my weight.
 */
public interface InformationModel {

	/**
	 * How many readings are there.
	 * 
	 * @return - the number of readings.
	 */
	int numberOfReadings();

	/**
	 * How many weeks since between the first and last recording.
	 * 
	 * @return - the number of weeks.
	 */
	int numberOfWeeks();

	/**
	 * When was the first recording made.
	 * 
	 * @return - the date of the earliest recording.
	 */
	String earliestDate();

	/**
	 * When was the latest recording made.
	 * 
	 * @return - the date of the most recent recording
	 */
	String latestDate();

	/**
	 * What was the weight at the earliest recording.
	 * 
	 * @return - the weight of the earliest recording.
	 */
	String earliestWeight();

	/**
	 * What was the weight at the latest recording.
	 * 
	 * @return - the weight of the latest recording.
	 */
	String latestWeight();

	/**
	 * What is the difference between the earliest and the latest weights.
	 * 
	 * @return - the difference.
	 */
	String changeInWeight();

	/**
	 * What is the lightest weight recorded.
	 * 
	 * @return - the lightest weight recorded.
	 */
	String lightestWeight();

	/**
	 * What is the heaviest weight recorded.
	 * 
	 * @return - the heaviest weight recorded.
	 */
	String heaviestWeight();

	/**
	 * What is the average of all the weights recorded.
	 * 
	 * @return - the average weight.
	 */
	String averageWeight();

	/**
	 * What is the average difference in weight per week.
	 * 
	 * @return - the average difference per week.
	 */
	String averageChangePerWeek();
}
