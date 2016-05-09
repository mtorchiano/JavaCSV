package it.polito.softeng.csvparser.examples;

import java.util.HashMap;
import java.util.Map;

import it.polito.softeng.csvparser.Processor;
import it.polito.softeng.csvparser.Row;

/**
 * Sample Process for the open-coesione data
 * 
 * The goal of this observer is to compute the sum
 * of the field FINANZ_UE aggregated by DPS_TEMA_SINTETICO
 * 
 * @author MTk (Marco Torchiano)
 *
 */
public class FinanzByTemaElab implements Processor {

	private static final String FIN = "FINANZ_UE";
	private static final String TEMA = "OC_TEMA_SINTETICO";
	private Map<String,Long> totali = new HashMap<String,Long>();
	private long count=0;

	public void newLine(Row r) {
		count++;
		String chiave = r.get(TEMA);
		String val = r.get(FIN);
		if( !val.isEmpty()){
			long valore = Long.parseLong(val.replaceAll(",",""));
			Long totale = totali.get(chiave);
			if( totale == null){ // first time the key is found
				totale = new Long(0);
			}
			totali.put(chiave, totale+valore);
		}
	}
	
	public String toString(){
		int temaWidth=0;
		long maxFin=0;
		for(String k : totali.keySet()){
			if(k.length()>temaWidth) temaWidth = k.length();
			long v = totali.get(k);
			if(v>maxFin) maxFin=v;
		}
		int finWidth = (int)(1+Math.ceil(Math.log10(maxFin)*1.3));
		String fmt="%"+temaWidth+"s:%,"+ finWidth + ".2f\n";
		StringBuffer res = new StringBuffer();
		res.append(String.format("%"+temaWidth+"s:%"+finWidth+"s\n",TEMA,FIN));
		for(String key : totali.keySet()){
			long valore = totali.get(key);
			double importo = ((double)valore) / 100;
			res.append(String.format(fmt,key,importo));
			// OR
//			long pi = valore / 100;
//			long pd = valore % 100;
//			System.out.println(chiave + " : " + pi + "." + pd);
		}
		return res.toString();
	}

	private long begin;
	public void headers(String[] titoli) {
		// Non fa nulla se non tracciare il tempo
		begin=System.nanoTime();
	}

	public void end() {
		// Non fa nulla se non tracciare il tempo
		long end=System.nanoTime();
		long elapsedUSec = (end-begin)/1000;
		System.out.println("Processed " + count + " lines");
		System.out.println("Elapsed " + (double)elapsedUSec/1000000 + " sec");
	}

}
