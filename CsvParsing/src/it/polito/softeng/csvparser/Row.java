package it.polito.softeng.csvparser;

import java.util.Map;

/**
 * Contain the information about the current row
 * 
 * @author MTk (Marco Torchiano)
 * @version 0.6
 */
public class Row {

	private Map<String,Integer> titoliIndici;
	private String[] campi;
	private long riga;
	
	Row(Map<String, Integer> titoliIndici, String[] campi, long count) {
		this.titoliIndici = titoliIndici;
		this.campi = campi;
		this.riga = count;
	}
	
	void init(String[] fields, long count) {
		this.campi = fields;
		this.riga = count;
	}
	
	/**
	 * Retrieves the cell identified by the index
	 * @param index the positional index
	 * @return the value of the cell
	 */
	public String get(int index){
		return campi[index];
	}
	
	/**
	 * Retrieves a cell by title of the column
	 * @param title the title of the column
	 * @return the value of the cell
	 */
	public String get(String title){
		return campi[titoliIndici.get(title)];
	}
	
	/**
	 * Number of the row
	 * @return the number of the current row
	 */
	public long getNum(){
		return riga;
	}
	
	/**
	 * Number of columns
	 * @return the number of columns
	 */
	public long getLength(){
		return campi.length;
	}
}
