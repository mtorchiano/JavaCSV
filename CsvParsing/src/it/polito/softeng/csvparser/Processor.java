package it.polito.softeng.csvparser;

/**
 * This interface must be implemented by the processors
 * used with the {@link CsvParser}.
 * 
 * @author mtk
 *
 */
public interface Processor {
	/**
	 * Method called after reading the first row of CSV that
	 * is expected to contain the headers of the columns (or field names)
	 * 
	 * @param headers  an array of {@link java.lang.String}s containing the field names
	 */
	void headers(String[] headers);
	
	/**
	 * Method called upon reading each row
	 * 
	 * @param row  an row object
	 */
	void newLine(Row row);
	
	/**
	 * Method called at the end of the file, before terminating the parsing, at the end of the file
	 */
	void end();
}
