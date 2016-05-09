package it.polito.softeng.csvparser;

import java.util.Map;

public class Row {

	private Map<String,Integer> titoliIndici;
	private String[] campi;
	private long riga;
	
	public Row(Map<String, Integer> titoliIndici, String[] campi, long count) {
		this.titoliIndici = titoliIndici;
		this.campi = campi;
		this.riga = count;
	}
	

	public String get(int indice){
		return campi[indice];
	}
	
	public String get(String titolo){
		return campi[titoliIndici.get(titolo)];
	}
	
	public long getNum(){
		return riga;
	}
	
	public long getLength(){
		return campi.length;
	}
}
