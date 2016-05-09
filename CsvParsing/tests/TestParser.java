import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import it.polito.softeng.csvparser.CsvParser;

public class TestParser {

	@Test
	public void testSemicolon() throws IOException {
		String csv = "A;B\n1;2\n3;4";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		p.parse(new BufferedReader(sr));
		System.out.println(CsvParser.printer);
		
	}

	@Test
	public void testColon() throws IOException {
		String csv = "A,B\n1,2\n3,4";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		p.parse(new BufferedReader(sr));
		System.out.println(CsvParser.printer);
		
	}


	@Test
	public void testQuoted() throws IOException {
		String csv = "\"A\",B\n1,\"2\"\n\"3\",4";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		p.parse(new BufferedReader(sr));
		System.out.println(CsvParser.printer);
		
	}
}
