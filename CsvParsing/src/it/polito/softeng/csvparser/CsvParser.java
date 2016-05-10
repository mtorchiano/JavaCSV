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
import java.util.regex.Matcher;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.regex.Pattern;

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
	private static final char[] CSV_SEPARATORS = {',',';','\t'};
	public static char CSV_SEPARATOR = ';';
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
		parse(in);
	}

	/**
	 * Avvia il "parsing" del file specificato.
	 * 
	 * @param filename  nome del file.
	 * @param encoding  specifica la codifica del file.
	 * @throws IOException
	 */
	public void parse(BufferedReader in) throws IOException{
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
		String[] dati = new String[titoli.length];
		while( (riga=in.readLine()) != null){
			count++;
			dati=split(riga.toCharArray(),dati);
//			dati=split(riga,dati);
			
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
	
	
	public void parsePar(String filename) throws IOException{
		parsePar(filename,"utf-8");
	}
	
	public void parsePar(String filename,String encoding) throws IOException{
		FileInputStream fr = new FileInputStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(fr,encoding));
		parsePar(in);
	}

	public void parsePar(BufferedReader in) throws IOException {
//		long count = 0;
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
		
		String[][] dati = {new String[titoli.length],
							new String[titoli.length]};
		boolean[] full = {false,false};
		Thread t=new Thread(()->{
			try {
				long count=0;
				int i=0;
				while(true){
					if(Thread.currentThread().isInterrupted()) return;
					synchronized(full){
						while(!full[i]) full.wait();
						Row r = new Row(titoliIndici,dati[i],count);
						for(Processor e : elaboratori){
							e.newLine(r);
						}
						full[i] = false;
						full.notifyAll();
					}
					i=1-i;
					count++;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		t.start();
		int i=0;
		while( (riga=in.readLine()) != null){
//			count++;
			synchronized(full){
				while(full[i])
					try {
						full.wait();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				split(riga.toCharArray(),dati[i]);
				full[i] = true;
				full.notifyAll();
			}
			i=1-i;
//			dati=split(riga,dati);
			
//			Row r = new Row(titoliIndici,dati,count);
//			for(Processor e : elaboratori){
//				e.newLine(r);
//			}
		}
		synchronized(full){
			while(full[0] || full[1])
				try {
					full.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		t.interrupt();
		for(Processor e : elaboratori){
			e.end();
		}
		in.close();
	}


	private static String[] split(String s){
		char[] chars = s.toCharArray();
		char sep = '\0';
		int max = 0;
		for(int si=0; si<CSV_SEPARATORS.length; ++si){
			int count = 0; 
			for(char ch : chars){
				if(ch==CSV_SEPARATORS[si]) count++;
			}		
			if(count>max){
				max=count;
				sep=CSV_SEPARATORS[si];
			}
		}
		CSV_SEPARATOR = sep;
		
//		rowPattern = Pattern.compile("((\"(([^\"]*|\"\")*)\")|([^\""+sep+"]*))("+sep+"|$)");

		
		String[] risultato = split(chars,new String[max+1]);
//		String[] risultato = split(s,new String[max+1]);
		for(int i=0; i<risultato.length; ++i){
			if(risultato[i]==null){
				return Arrays.copyOf(risultato,i);
			}
		}
		return risultato;
	}

	private static String[] split(char[] chars,String[] risultato){
		int p=0;

		boolean inQuotes = false;
		boolean postQuotes = false;
		final int n = chars.length;
		int begin=0;
		int j=0;
		for( int i =0 ; i<n; ++i,++j){
			char ch = chars[i];
			if(j<i) chars[j] = ch;
			if(inQuotes){
				if(ch==CSV_QUOTATOR){
					if(i==n-1 || chars[i+1]!=CSV_QUOTATOR){
						inQuotes=false;
						postQuotes = true;
						risultato[p++] = new String(chars,begin,j-begin);
					}else{ // double " == quotation
						i++;
					}
				}
			}else{
				if(ch==CSV_SEPARATOR){
					if(postQuotes){
						postQuotes = false;
					}else{
						risultato[p++] = new String(chars,begin,i-begin);
					}
					begin=i+1;
					j=i;
				}else{
					if(ch==CSV_QUOTATOR){
						begin=i+1;
						inQuotes=true;
					}
				}
			}
			
		}
		if(!postQuotes)
			risultato[p++]= new String(chars,begin,n-begin);
		return risultato;
	}
	
// More elegant but twice slower than the previous ad-hoc version :-/
//
//	private static Pattern rowPattern = Pattern.compile(
//			"((\"(([^\"]*|\"\")*)\")|([^\";]+))(;|$)");
//
//	private static String[] split(String s,String [] risultato){
//		Matcher m = rowPattern.matcher(s);
//		int i=0;
//		int n = risultato.length;
//		while(i<n && m.find()){
//			String r = m.group(3);
//			if(r==null) r = m.group(5);
//			risultato[i++] = r;
//		}
//		return risultato;
//	}
//	
	private static class PrinterProcessor implements Processor {

	private StringBuffer output; 
	@Override
	public void headers(String[] headers) {
		output = new StringBuffer();
		for(String h : headers){
			output.append(h);
			output.append(",");
		}		
		output.append("\n");
	}

	@Override
	public void newLine(Row row) {
		for(int i=0; i<row.getLength(); ++i){
			output.append(row.get(i));
			output.append(",");
		}
		output.append("\n");
	}

	@Override
	public void end() {
		
	}
	
	public String toString(){
		return output.toString();
	}
	}
	
	public final static Processor printer = new PrinterProcessor();

}
