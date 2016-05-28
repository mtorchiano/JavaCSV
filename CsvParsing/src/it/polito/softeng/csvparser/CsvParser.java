package it.polito.softeng.csvparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class implements a high-performance streaming parser for CSV files.
 * <p>
 * It is based on the Observer pattern, where the parser generates 
 * events that are notified to one of more {@link Processor} objects
 * with the purpose of processing the data contained in the elements 
 * of the CSV.
 * <p>
 * The parser conforms with <a href="https://tools.ietf.org/html/rfc4180">IETF RFC 4180</a>.
 * <p>
 * The typical usage for a streaming parser: 
 * 
 * <pre>
	CsvParser p = new CsvParser("file.csv");
	
	// empty cells processor
	Processor proc = new EmptyCells();
	p.addProcessor(proc);
	
	// start parsing
	Stats s = p.parse();
</pre>

 * 
 * @author MTk (Marco Torchiano)
 * @version 0.6
 *
 */
public class CsvParser {
	
	private static final char[] CSV_SEPARATORS = {',',';','\t'};
	private static char CSV_SEPARATOR = ';';
	private List<Processor> processors = new LinkedList<Processor>();
	private Reader in;

	/**
	 * Build a parser for the given file
	 * 
	 * @param filename full path of the file to be opened
	 * 
	 * @throws IOException if any IO problem is detected
	 */
	public CsvParser(String filename) throws IOException{
		this(new FileInputStream(filename));
	}

	/**
	 * Build a parser for the given file
	 * 
	 * @param filename full path of the file to be opened
	 * @param encoding the encoding to be used to open the file
	 * 
	 * @throws IOException if any IO problem is detected
	 */
	public CsvParser(String filename,String encoding) throws IOException{
		this(new FileInputStream(filename),encoding);
	}

	/**
	 * Build a parser for the given file
	 * 
	 * @param ins input stream to read the CSV from
	 * 
	 * @throws IOException if any IO problem is detected
	 */
	public CsvParser(InputStream ins) throws IOException{
		in = new InputStreamReader(ins);
	}

	/**
	 * Build a parser for the given file
	 * 
	 * @param ins input stream to read the CSV from
	 * @param encoding the encoding to be used to open the file
	 * 
	 * @throws IOException if any IO problem is detected
	 */
	public CsvParser(InputStream ins,String encoding) throws IOException{
		in = new InputStreamReader(ins,encoding);
	}


	/**
	 * Build a parser for the given file
	 * 
	 * @param in input reader to read the CSV from
	 * 
	 * @throws IOException if any IO problem is detected
	 */
	public CsvParser(Reader in) throws IOException{
		this.in = in;
	}

	/**
	 * Adds a new processor for the parsed data.
	 * 
	 * It is possible to have several processors for the
	 * same parser that manange the information independently
	 * the data contained in the CSV.
	 * 
	 * @param proc  the processor object implmenting the {@link Processor} interface
	 */
	public void addProcessor(Processor proc){
		processors.add(proc);
	}
	
	
	
	private char[] buffer = new char[4096];
	private int begin;
	private int current;
	private int end;
	private int limit;
	private long row;
	private long cells;
	private long charCount=0;
	private int col;
	private String[] fields;
	private Map<String,Integer> titoliIndici;
	private ArrayList<String> titoliList = new ArrayList<String>();
	
	private void start() throws IOException{
		limit = in.read(buffer);
		begin = -1;
		end = -1;
		current = 0;
		charCount+=limit;
		row=0;
		
		
		String s = new String(buffer);
		long max = 0;
		for(int i=0; i<CSV_SEPARATORS.length; ++i){
			char sep =CSV_SEPARATORS[i];
			long ns = s.chars().filter(ch -> ch == sep ).count();
			if(ns>max){
				max = ns;
				CSV_SEPARATOR = sep;
			}
		}

	}
	
	private int next() throws IOException{
		if(current==limit){
			if(begin>0){
				System.arraycopy(buffer, begin, buffer, 0, limit-begin);
				int ns=begin;
				begin=0;
				end-=ns;
				int nl = in.read(buffer, limit-ns, ns);
				if(nl==-1){
					return -1;
				}
				charCount+=nl;
				current=limit-ns;
				limit-=ns-nl;
			}else{
				char[] newBuffer = new char[buffer.length*2];
				System.arraycopy(buffer,0, newBuffer, 0, buffer.length);
				int nl = in.read(newBuffer, limit, buffer.length);
				if(nl==-1) return -1;
				charCount+=nl;
				limit+=nl;
				buffer=newBuffer;
			}
		}
		return buffer[current++];
	}
	
	private void beginfield(){
		begin=current-1;
		end=begin;
	}
	
	private void closefield(){
		String f = new String(buffer,begin,end-begin);
		if(fields!=null){
			fields[col] = f;
		}else{
			titoliList.add(f);
		}
		col++;
		cells++;
		//System.out.print("'" + f +  "'  ");
	}
	
	private Row currentRow;;
	private void endrow(){
		if(row==0){
			titoliIndici = new HashMap<String,Integer>();
			for(int i=0; i<titoliList.size(); ++i){
				titoliIndici.put(titoliList.get(i), i);
			}
			fields=titoliList.toArray(new String[titoliList.size()]);
			for(Processor e : processors){
				e.headers(fields);
			}
			currentRow = new Row(titoliIndici,fields,0);
		}else{
			//Row r = new Row(titoliIndici,fields,row);
			currentRow.init(fields, row);
			for(Processor e : processors){
				e.newLine(currentRow);
			}			
		}
		col=0;
		row++;
		begin=end;
		//System.out.print("\n");
	}
	
	private void addtofield(){
		if(end==current-1){
			end++;
		}else{
			buffer[end++] = buffer[current-1];
		}
	}
	
	private static final int START=0;
	private static final int UNQUOTED=1;
	private static final int ENDROW=2;
	private static final int QUOTED=3;
	private static final int QUOTEBEGIN=6;
	private static final int DQUOTE=4;
	private static final int CR=5;
	
//	private static final int END=-1;
	private static final int EOF = -1;
	
	/**
	 * Class that contains the parsing statistics.
	 * 
	 * @author mtk
	 *
	 */
	
	public class Stats {
		/** Parsing elapsed time */
		public final Duration elapsed;

		/** Parsing number of rows */
		public final long rows;

		/** Parsing number of characters */
		public final long chars;

		/** Parsing number of cells */
		public final long cells;
		
		Stats(Duration e, long r, long i, long c){
			elapsed = e;
			rows = r;
			cells = i;
			chars = c;
		}

		/**
		 * Computes the throughput
		 * 
		 * @return Million lines per second
		 */
		public double throughput(){
			return chars/1000000.0 / (elapsed.getSeconds()+elapsed.getNano()/1000000000.0);
		}

		public String toString(){
			return "Processed " + chars + " chars, " + cells + " cells, "+ row + " rows, in " + elapsed+
					" : throughput: " + String.format("%.3f",throughput()) +
					"Mch/s";
		}
	}
	
	/**
	 * Start the parsing of the CSV content
	 * 
	 * @return the parsing statistics
	 * 
	 * @throws IOException in case of I/O error
	 */
	public Stats parse() throws IOException{

		Instant beginTime = Instant.now();
		start();
		int state = START;
		while(true){
			int ch = next();
			if(ch==EOF){
				if(col>0 || end>begin){
					closefield();
	  		  		endrow();
				}
	  			for(Processor e : processors){
	  				e.end();
	  			}
	  			Instant endTime = Instant.now();
	  			return new Stats(Duration.between(beginTime, endTime),row,cells,charCount);
			}
			switch(state){
			case START:
				switch(ch){
				case '"': state = QUOTEBEGIN;
						  break;
				case ',': if(CSV_SEPARATOR==','){
								beginfield();
								closefield();
							    state = START;
						  }else{
							  beginfield();
							  addtofield();
						  }
						  break;
				case ';': if(CSV_SEPARATOR==';'){ 
								beginfield();
								closefield();
							    state = START;
						  }else{
							  beginfield();
							  addtofield();
						  }
						  break;
				default: beginfield();
						 addtofield();
						 state = UNQUOTED;
						 break;
				}
				break;
			case UNQUOTED :
				switch(ch){
				case ',': if(CSV_SEPARATOR==','){ 
								closefield();
							    state = START;
						  }else{
							  addtofield();
						  }
						  break;
				case ';': if(CSV_SEPARATOR==';'){ 
								closefield();
							    state = START;
						  }else{
							  addtofield();
						  }
						  break;
				case '\r':closefield();
				  		  endrow();
				  		  state = ENDROW;
				  		  break;
				case '\n':closefield();
						  endrow();
				  		  state = ENDROW;
				  		  break;
				default:  addtofield();
				}
				break;
			case ENDROW :
				switch(ch){
				case '\n'://state = START;
						  break;
				case '\r'://state = START;
						  break;
				case '"': state = QUOTEBEGIN;
						  break;
				case ',': if(CSV_SEPARATOR==','){
								beginfield();
								closefield();
							    state = START;
						  }else{
							  beginfield();
							  addtofield();
						  }
						  break;
				case ';': if(CSV_SEPARATOR==';'){ 
								beginfield();
								closefield();
							    state = START;
						  }else{
							  beginfield();
							  addtofield();
						  }
						  break;
				default:  beginfield();
						  addtofield();
						  state = UNQUOTED;
				}
				break;
			case QUOTEBEGIN :
				switch(ch){
				case '"': beginfield();
						  state = DQUOTE;
						  break;
				case '\n':beginfield();
						  addtofield();
				  		  state = CR;
				  		  break;
				default: beginfield();
						 addtofield();
						 state=QUOTED;
				}
				break;
			case QUOTED :
				switch(ch){
				case '"': state = DQUOTE;
						  break;
				case '\n':addtofield();
				  		  state = CR;
				  		  break;
				default: addtofield();
				}
				break;
			case DQUOTE :
				switch(ch){
				case '"': state = QUOTED;
						  addtofield();
						  break;
				case ',': if(CSV_SEPARATOR==','){
								closefield();
							    state = START;
						  }else{
							  addtofield();
						  }
						  break;
				case ';': if(CSV_SEPARATOR==';'){ 
								closefield();
							    state = START;
						  }else{
							  addtofield();
						  }
						  break;
				case '\n':closefield();
						  endrow();
				  		  state = ENDROW;
				  		  break;
				case '\r':closefield();
				  		  endrow();
				  		  state = ENDROW;
				  		  break;
				default: /* unexpected char */
						 addtofield();
				}
				break;
			case CR :
				switch(ch){
				case '\r':state = QUOTED;
						  break;
				case '"':state = DQUOTE;
						 break;
				default: addtofield();
						 state = QUOTED;
				}
				break;
			}
		}
	}
}
