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
		CsvParser.Stats s = p.parse(new BufferedReader(sr));
		//System.out.println(CsvParser.printer);
		assertEquals(3,s.rows);
	}

	@Test
	public void testColon() throws IOException {
		String csv = "A,B\n1,2\n3,4";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		CsvParser.Stats s = p.parse(new BufferedReader(sr));
		//System.out.println(CsvParser.printer);
		assertEquals(3,s.rows);
	}


	@Test
	public void testQuoted() throws IOException {
		String csv = "\"A\",B\n1,\"2\"\n\"3\",4";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		CsvParser.Stats s = p.parse(new BufferedReader(sr));
		//System.out.println(CsvParser.printer);
		assertEquals(3,s.rows);
	}

	@Test
	public void testExtraCRLF() throws IOException {
		String csv = "A,B\r\n1,\"2\"\r\n\"3\",4\r\n";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		CsvParser.Stats s = p.parse(new BufferedReader(sr));
		//System.out.println(CsvParser.printer);
		assertEquals(3,s.rows);
	}

	@Test
	public void testDQ() throws IOException {
		String csv = "A,B\r\n1,\"2 is \"\"two\"\" \"\r\n\"3  is \"\"three\"\"\",4\r\n";
		
		StringReader sr = new StringReader(csv);
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		CsvParser.Stats s = p.parse(new BufferedReader(sr));
		System.out.println(CsvParser.printer);
		assertEquals(3,s.rows);
	}

	@Test
	public void testScuole() throws IOException {
		
		CsvParser p = new CsvParser();
		p.addProcessor(CsvParser.printer);
		CsvParser.Stats s = p.parse("scuole.csv");
		//System.out.println(CsvParser.printer);
		assertEquals(4378,s.rows);
		System.out.println("Elapsed: " + s.elapsed);
	}

}
