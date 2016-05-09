package it.polito.softeng.csvparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 * Classe per la lettura in modalita' streaming di un file CSV.
 * 
 * Si basa sul pattern Observer, dove il parser genera degli eventi
 * che vengono inviati ad uno o piu' oggetti Elaboratore che hanno
 * il compito di elaborare i dati contenuti nelle linee del file CSV.
 * 
 * @author MTk (Marco Torchiano)
 *
 */
public class CsvParser {
	
	private static final char CSV_QUOTATOR = '"';
	public static final char CSV_SEPARATOR = ';';
	private List<Processor> elaboratori = new LinkedList<Processor>();

	/**
	 * Aggiunge un elaboratore di linee al parser.
	 * 
	 * E' possibile avere piu' elaboratori per lo stesso parser
	 * che gestiscono indipendentemente le informazioni delle
	 * righe contenute nel file CSV.
	 * 
	 * @param e  l'oggetto che implementa l'interfaccia Elaboratore
	 */
	public void addProcessor(Processor e){
		elaboratori.add(e);
	}
	
	/**
	 * Avvia il "parsing" del file specificato.
	 * 
	 * @param filename  nome del file.
	 * @throws IOException
	 */
	public void parse(String filename) throws IOException{
		parse(filename,"utf-8");
	}
	
	/**
	 * Avvia il "parsing" del file specificato.
	 * 
	 * @param filename  nome del file.
	 * @param encoding  specifica la codifica del file.
	 * @throws IOException
	 */
	public void parse(String filename,String encoding) throws IOException{
		FileInputStream fr = new FileInputStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(fr,encoding));
		
		long count = 0;
		String riga;
		String prima = in.readLine();
		
		String[] titoli = split(prima);
		Map<String,Integer> titoliIndici = new HashMap<String,Integer>();
		for(int i=0; i<titoli.length; ++i){
			titoliIndici.put(titoli[i], i);
		}
		for(Processor e : elaboratori){
			e.headers(titoli);
		}
		String[] dati = new String[titoli.length+1];
		while( (riga=in.readLine()) != null){
			count++;
			dati=split(riga,dati);
			
			Row r = new Row(titoliIndici,dati,count);
			for(Processor e : elaboratori){
				e.newLine(r);
			}
		}
		for(Processor e : elaboratori){
			e.end();
		}
		in.close();
	}

	private static String[] split(String s){
		int count = 0; 
		for(char ch : s.toCharArray()){
			if(ch==CSV_SEPARATOR) count++;
		}
		String[] risultato = split(s,new String[count+1]);
		for(int i=0; i<risultato.length; ++i){
			if(risultato[i]==null){
				return Arrays.copyOf(risultato,i);
			}
		}
		return risultato;
	}

	private static String[] split(String s,String[] risultato){
		int p=0;

		boolean inQuotes = false;
		StringBuffer campo = new StringBuffer();
		final int n = s.length();
		for( int i =0 ; i<n; ++i){
			char ch = s.charAt(i);
			if(inQuotes){
				campo.append(ch);
				if(ch==CSV_QUOTATOR){
					if(i==n-1 || s.charAt(i+1)!=CSV_QUOTATOR){
						inQuotes=false;
						campo.setLength(campo.length()-1);
					}else{ // double " == quotation
						i++;
					}
				}
			}else{
				if(ch==CSV_SEPARATOR){
					risultato[p++]= campo.toString();
					campo.delete(0, campo.length());
				}else{
					if(ch==CSV_QUOTATOR){
						inQuotes=true;
					}else{
						campo.append(ch);
					}
				}
			}
			
		}
		return risultato;
	}
	
// More elengant but twice slower than the previous ad-hoc version :-/
//
//	private static Pattern rowPattern = Pattern.compile(
//			"((\"(([^\"]*|\"\")*)\")|([^\";]*))(;|$)");
//
//	private static String[] split(String s,String [] risultato){
//		Matcher m = rowPattern.matcher(s);
//		int i=0;
//		while(m.find()){
//			String r = m.group(3);
//			if(r==null) r = m.group(5);
//			risultato[i++] = r;
//		}
//		return risultato;
//	}

}
