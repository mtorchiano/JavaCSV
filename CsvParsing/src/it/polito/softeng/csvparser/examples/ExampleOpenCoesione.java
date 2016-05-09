package it.polito.softeng.csvparser.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.polito.softeng.csvparser.CsvParser;
import it.polito.softeng.csvparser.Processor;

/**
 * Main class per l'esempio sull'analisi dei dati di open-coesione.
 * 
 * Tutta l'elaborazione e' svolta nella classe 
 * {@link it.Processor.cvsparser.Elaboratore Elaboratore}
 * 
 * I dati sono prelevati dal set di open-data di opencoesione.gov.it
 * relativi ai fondi strutturali Europei 2007/2013.
 * 
 * @author MTk
 * @see <a href="http://www.opencoesione.gov.it/opendata/">Open Coesione - Open Data</a>
 */
public class ExampleOpenCoesione {

	public static void main(String[] args) throws IOException {
		
		// Create the parser
		CsvParser p = new CsvParser();
		
		// Instantiate the processor
		Processor e = new FinanzByTemaElab();
		
		// Register the processor with the parser
		//p.addProcessor(e);
		
		e = new EmptyCells();
		p.addProcessor(e);
		
		if(download("progetti_FS0713_20151231.csv")){
			// start parsing
			p.parse("progetti_FS0713_20151231.csv");
		
			// prints results (accumulated into the processor)
			System.out.println(e.toString());
		}
	}

	private static boolean download(String file) {
		if((new File(file)).exists()){
			System.out.println("Opening local copy of file");
		}else{
			System.out.println("Downloading open-data from opencoesione...");
			try {
//				URL url = new URL("http://www.opencoesione.gov.it/opendata/progetti_FS0713_20131231.zip"); // 2013 version
				URL url = new URL("http://www.opencoesione.gov.it/opendata/progetti_FS0713.zip");
				InputStream in = url.openStream();
				ZipInputStream zip = new ZipInputStream(in);
				while(zip.available()>0){
					ZipEntry entry = zip.getNextEntry();
					FileOutputStream out = new FileOutputStream(entry.getName());
					byte[] buffer=new byte[4096];
					int n;
					while((n=zip.read(buffer)) > 0){
						out.write(buffer,0,n);
					}
					out.close();
					zip.closeEntry();
				}
				System.out.println("Done!");
			} catch (Exception e) {
				System.err.println("Sorry I cannot download the file...");
				System.err.println("Cause:");
				e.printStackTrace();
				return false;
			}
		}
		return true;		
	}

}
