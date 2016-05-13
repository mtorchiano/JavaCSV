package it.polito.softeng.csvparser.processor;

import it.polito.softeng.csvparser.Processor;
import it.polito.softeng.csvparser.Row;

public class Printer implements Processor {

	private StringBuffer output;

	@Override
	public void headers(String[] headers) {
		output = new StringBuffer();
		for (String h : headers) {
			output.append(h);
			output.append(",");
		}
		output.append("\n");
	}

	@Override
	public void newLine(Row row) {
		for (int i = 0; i < row.getLength(); ++i) {
			if (i != 0)
				output.append(",");
			output.append(row.get(i));
		}
		output.append("\n");
	}

	@Override
	public void end() {

	}

	public String toString() {
		return output.toString();
	}
}
