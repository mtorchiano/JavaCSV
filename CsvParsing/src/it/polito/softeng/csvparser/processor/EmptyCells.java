package it.polito.softeng.csvparser.processor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import it.polito.softeng.csvparser.Processor;
import it.polito.softeng.csvparser.Row;

/**
 * Data quality sample processor.
 * 
 * Computes empty cells, rows, columns
 * 
 * @author MTk (Marco Torchiano)
 *
 */
public class EmptyCells implements Processor {

	private long lines=0;
	private long cells=0;
	private long empty=0;
	private long emptyLines=0;
	private long n=-1;
	private Map<Long,Long> lengths = new HashMap<>();
	private static final Long ZERO = new Long(0);

	public void newLine(Row r) {
		lines++;
		boolean emptyLine=true;
		for(int i=0; i<n; ++i){
			String c=r.get(i);
			if(c==null || c.equals("")){
				empty++;
				emptyLine&=emptyLine;
			}else{
				emptyLine=false;
			}
		}
		if(emptyLine) emptyLines++;
		long n = lengths.getOrDefault(r.getLength(), ZERO);
		lengths.put(r.getLength(), ++n);
	}
	
	public String toString(){
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		StringBuffer res = new StringBuffer();
		res.append("Empty cells: ").append(empty).append(" out of ").append(cells).append(" (").append(nf.format(empty/(double)cells) +")\n"
				).append("Empty lines: ").append(emptyLines).append(" out of ").append(lines).append(" (").append(nf.format(emptyLines/(double)lines)).append(")\n"
				);
		
		res.append("Row lengths:\n");
		for(Long l : lengths.keySet()){
			res.append(l).append(" : ").append(lengths.get(l)).append("\n");
		}
		
		return res.toString();
	}

	private long begin;
	public void headers(String[] titoli) {
		n = titoli.length;
		// Non fa nulla se non tracciare il tempo
		begin=System.nanoTime();
	}

	public void end() {
		cells = lines*n;
		// Non fa nulla se non tracciare il tempo
		long end=System.nanoTime();
		long elapsedUSec = (end-begin)/1000;
		System.out.println("Processed " + lines + " lines");
		System.out.println("Elapsed " + (double)elapsedUSec/1000000 + " sec");
	}

}
