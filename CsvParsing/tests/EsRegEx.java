import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EsRegEx {

	public static void main(String[] args) {
		
		String regexp = "[+-]?[0-9]+";
		
		Pattern p = Pattern.compile(regexp);  // genera DFA
		
		String s = "+123";
		
		Matcher m = p.matcher(s);  // Avvio la macchina a stati
		if( m.matches() ){ // eseguo il riconoscimento
			System.out.println("'" + s +"' è un numero intero con segno");
		}

		s = "-76 x";
		m = p.matcher(s);  // Avvio la macchina a stati
		if( m.lookingAt() ){ // eseguo il riconoscimento
			System.out.println("'" + s +"' inizia con un numero intero con segno");
		}else{
			System.out.println("'" + s +"' NON è un numero intero con segno");
		}
		
		s = "questo è un numero intero: +42 .";
		m = p.matcher(s);  // Avvio la macchina a stati
		if( m.find() ){ // eseguo il riconoscimento
			System.out.println("'" + s +"' contiene un numero intero con segno");
			System.out.println("   il numero è: " + m.group());
//			int inizio = m.start();
//			int fine = m.end();
		}else{
			System.out.println("'" + s +"' NON è un numero intero con segno");
		}
		
		String linea = "nome : John Smith";
		
		regexp = "([a-zA-Z_][0-9a-zA-Z_]*)[ \t]*:[ \t]*([A-Z].*)";
		
		p = Pattern.compile(regexp);
		
		m = p.matcher(linea);
		if( m.matches()){
			System.out.println("Ok.");
			System.out.println("id: " + m.group(1));
			System.out.println("id: " + m.group(2));
		}else{
			System.out.println("NON ok.");
		}
	}

}
